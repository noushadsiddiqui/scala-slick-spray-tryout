package com.tryout.scalaslick.rest

import akka.actor.Actor
import akka.event.slf4j.SLF4JLogging
import java.text.{ParseException, SimpleDateFormat}
import java.util.Date
import com.tryout.scalaslick.dao.CustomerDAO
import com.tryout.scalaslick.domain.{Customer, Failure, CustomerSearchParameters}
import net.liftweb.json.Serialization._
import net.liftweb.json.{DateFormat, Formats}
import spray.http._
import spray.httpx.unmarshalling._
import spray.routing._

class RestServiceActor extends Actor with RestService {

  implicit def actorRefFactory = context

  def receive = runRoute(rest)
}

trait RestService extends HttpService with SLF4JLogging {

  val customerService = new CustomerDAO

  implicit val executionContext = actorRefFactory.dispatcher

  implicit val liftJsonFormats = new Formats {
    val dateFormat = new DateFormat {
      val sdf = new SimpleDateFormat("yyyy-MM-dd")

      def parse(s: String): Option[Date] = try {
        Some(sdf.parse(s))
      } catch {
        case e: Exception => None
      }

      def format(d: Date): String = sdf.format(d)
    }
  }

  implicit val string2Date = new FromStringDeserializer[Date] {
    def apply(value: String) = {
      val sdf = new SimpleDateFormat("yyyy-MM-dd")
      try Right(sdf.parse(value))
      catch {
        case e: ParseException => {
          Left(MalformedContent("'%s' is not a valid Date value" format (value), e))
        }
      }
    }
  }

  implicit val customRejectionHandler = RejectionHandler {
    case rejections => mapHttpResponse {
      response =>
        response.withEntity(HttpEntity(ContentType(MediaTypes.`application/json`),
          write(Map("error" -> response.entity.asString))))
    } {
      RejectionHandler.Default(rejections)
    }
  }

  val rest = respondWithMediaType(MediaTypes.`application/json`) {
    path("customer") {
      post {
        entity(Unmarshaller(MediaTypes.`application/json`) {
          case httpEntity: HttpEntity =>
            read[Customer](httpEntity.asString(HttpCharsets.`UTF-8`))
        }) {
          customer: Customer =>
            ctx: RequestContext =>
              handleRequest(ctx, StatusCodes.Created) {
                log.info("Creating customer: %s".format(customer))
                customerService.create(customer)
              }
        }
      } ~
        get {
          parameters('id.as[Long] ?, 'firstName.as[String] ?, 'lastName.as[String] ?, 'emailId.as[String] ?, 'birthday.as[Date] ?).as(CustomerSearchParameters) {
            searchParameters: CustomerSearchParameters => {
              ctx: RequestContext =>
                handleRequest(ctx) {
                  log.info("get customer list, parameters: %s".format(searchParameters))
                  customerService.search(searchParameters)
                }
            }
          }
        }
    } ~
      path("customer" / LongNumber) {
        customerId =>
          put {
            entity(Unmarshaller(MediaTypes.`application/json`) {
              case httpEntity: HttpEntity =>
                read[Customer](httpEntity.asString(HttpCharsets.`UTF-8`))
            }) {
              customer: Customer =>
                ctx: RequestContext =>
                  handleRequest(ctx) {
                    log.info("Updating customer, id %d: %s".format(customerId, customer))
                    customerService.update(customerId, customer)
                  }
            }
          } ~
            delete {
              ctx: RequestContext =>
                handleRequest(ctx) {
                  log.info("Deleting customer, id %d".format(customerId))
                  customerService.delete(customerId)
                }
            } ~
            get {
              ctx: RequestContext =>
                handleRequest(ctx) {
                  log.info("Retrieving customer, id %d".format(customerId))
                  customerService.get(customerId)
                }
            }
      }
  }

  /**
   * Handles an incoming request and create valid response for it.
   *
   * @param ctx         request context
   * @param successCode HTTP Status code for success
   * @param action      action to perform
   */
  protected def handleRequest(ctx: RequestContext, successCode: StatusCode = StatusCodes.OK)(action: => Either[Failure, _]) {
    action match {
      case Right(result: Object) =>
        ctx.complete(successCode, write(result))
      case Left(error: Failure) =>
        ctx.complete(error.getStatusCode, net.liftweb.json.Serialization.write(Map("error" -> error.message)))
      case _ =>
        ctx.complete(StatusCodes.InternalServerError)
    }
  }
}