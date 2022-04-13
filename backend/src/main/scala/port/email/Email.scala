package port.email

import entity.*

// Extend to suit the business logic needs
trait Email:
  def to: EmailAddress
  def subject: EmailSubject
  def content: EmailContent
