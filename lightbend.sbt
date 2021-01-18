
resolvers in ThisBuild += "lightbend-commercial-mvn" at
        "https://repo.lightbend.com/pass/V6GlIKHTHRPVKH4d-_qfpJDxeWg3kPrq1lqfrcEh2NDJwoWF/commercial-releases"
resolvers in ThisBuild += Resolver.url("lightbend-commercial-ivy",
        url("https://repo.lightbend.com/pass/V6GlIKHTHRPVKH4d-_qfpJDxeWg3kPrq1lqfrcEh2NDJwoWF/commercial-releases"))(Resolver.ivyStylePatterns)