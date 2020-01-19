# Crux Docker

Project to start running [Crux] on docker.

## Configuration

### Environment Variables

| Name                        | Description                                     | Example           |
| --------------------------- | ----------------------------------------------- | ----------------- |
| LOG_DIR                     | Logging directory                               | `/var/crux/logs`  |
| HEALTH_CHECK_THRESHOLD      | Number of health check failures before shutdown | 3                 |
| HEALTH_CHECK_INTERVAL       | Frequency of health checks (sec)                | 5                 |
| HEALTH_CHECK_WAIT_TIME      | Wait time before starting health checks (sec)   | 30                |
| STORAGE_DIR                 | Data storage directory                          | `/dev/crrux-data` |
| KAFKA_BOOTSTRAP_SERVER      | Kafka Bootstrap Server Address                  | `localhost:9092`  |
| SERVER_PORT                 | Crux HTTP API Server Port                       | 3000              |

## License

Copyright © 2020 FIXME

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.

[Crux]:https://github.com/juxt/crux