package port.email

import domain.*

// Extend to suit the business logic needs
trait Email:
  def to: EmailAddress
  def subject: EmailSubject
  def content: EmailContent
