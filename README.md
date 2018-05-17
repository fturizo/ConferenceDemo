# Conference Demo

This small sample project showcase the development of 3 micro-services using both Java EE 8 and Eclipse MicroProfile 1.2.

The domain model and real-world requirements are inspired in the [official conference demo project](https://github.com/eclipse/microprofile-conference "Conference Demo") for the Eclipse MicroProfile stack.

## Services

The following services are defined:

1. **Speaker**: Handles all speakers data and provides a default configurable list of venues.
2. **Session**: Handles session data. Unlike the official Conference demo, here scheduling sessions is doing in tandem while creating them, so no `schedule` service is used.
3. **Vote**: Handles session rating and the creation of attendees.

## Provisioning

All three services are provisioned using [Payara Micro 5](https://www.payara.fish/payara_micro). The Payara Micro Maven Plugin is configured on all three projects.
