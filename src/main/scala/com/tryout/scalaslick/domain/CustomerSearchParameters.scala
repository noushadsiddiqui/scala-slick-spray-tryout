package com.tryout.scalaslick.domain

import java.util.Date

/**
 * Customers search parameters.
 * @param id        db id
 * @param firstName first name
 * @param lastName  last name
 * @param emailId   email Id
 * @param birthday  dob
 */
case class CustomerSearchParameters(id :  Option[Long] = None,
                                    firstName: Option[String] = None,
                                    lastName: Option[String] = None,
                                    emailId: Option[String] = None,
                                    birthday: Option[Date] = None)