package config

object EnvVars:
  val SERVER_ENVIRONMENT      = "SERVER_ENVIRONMENT"
  val SERVER_HOST             = "SERVER_HOST"
  val SERVER_PORT             = "SERVER_PORT"
  val SERVER_SHUTDOWN_TIMEOUT = "SERVER_SHUTDOWN_TIMEOUT"

  val PURGE_INTERVAL = "PURGE_INTERVAL"

  val FRONTEND_BASE_URI = "BASE_FRONTEND_URI"

  val INVITE_TO_POLL_SUBJECT = "INVITE_TO_POLL_SUBJECT"
  val INVITE_TO_POLL_CONTENT = "INVITE_TO_POLL_CONTENT"

  val EMAIL_PORT_STRATEGY = "EMAIL_PORT_STRATEGY"
  val GMAIL_USERNAME      = "GMAIL_USERNAME"
  val GMAIL_PASSWORD      = "GMAIL_PASSWORD"
