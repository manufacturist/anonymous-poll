package endpoint

object HealthEndpoint:

  val health: Endpoint[Unit, Unit, Unit, Unit, Any] =
    baseEndpoint.get
      .name("Health check")
      .description("Indicates that the server is up and running")
      .in("health")
      .out(emptyOutput)
      .out(statusCode(StatusCode.Ok))
