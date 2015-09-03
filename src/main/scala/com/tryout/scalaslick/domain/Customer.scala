package com.tryout.scalaslick.domain

import scala.slick.driver.MySQLDriver.simple._

/**
 * Customer entity.
 *
 * @param id        unique id
 * @param firstName first name
 * @param lastName  last name
 * @param emailId   email of customer
 * @param birthday  date of birth
 * @param deleted   customer deleted or active
 */
case class Customer(id: Option[Long], firstName: String, lastName: Option[String], emailId: String, birthday: Option[java.util.Date], deleted: Boolean)

/**
 * Mapped customers table object.
 */
object Customers extends Table[Customer]("customers") {

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def firstName = column[String]("first_name")

  def lastName = column[String]("last_name", O.Nullable)

  def emailId = column[String]("email_id")

  def birthday = column[java.util.Date]("dob", O.Nullable)

  def deleted = column[Boolean]()

  // FK Example, user_FK : FK name, owner : column name here, Users : users table mapping
  // def withuser = foreignKey("User_FK", owner, Users)(_.id)

  def * = id.? ~ firstName ~ lastName.? ~ emailId ~ birthday.? ~ deleted <> (Customer, Customer.unapply _)

  implicit val dateTypeMapper = MappedTypeMapper.base[java.util.Date, java.sql.Date](
  {
    ud => new java.sql.Date(ud.getTime)
  }, {
    sd => new java.util.Date(sd.getTime)
  })

  val findById = for {
    id <- Parameters[Long]
    c <- this if c.id is id
  } yield c
}