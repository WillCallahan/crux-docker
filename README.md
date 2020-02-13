# Crux Docker

Project to start running [Crux] on docker in AWS ECS.

The Crux-Docker project provides a template with two key goals; a main function capable of running Crux in a
production environment and a container to host the Crux service.

## Pre-Conditions

This library assumes that you have an AWS account that follows best practices for infrastructure. That being said, you
are expected to have isolated CloudFormation stacks that each have dedicated purposes. 

The host AWS account must have access to a Kafka cluster as the Kafka cluster will act as the primary data store for
data indexed by Crux.

## Issues

Feel free to submit issues at any time! Please include steps to reproduce any issue you encounter.

You may also use issues to submit questions. Questions will result in more detailed documentation.

## Configuration

### AWS Configuration

#### Security Groups

The Crux service expects that a Kafka service is running. Either the AWS Managed Service for Kafka (MSK) or a manual
service of Kafka can be used as the data store for Crux. In order for the Crux service to connect to Kafka, you should
grant access to the `crux-host-<stack-name>` security group on ports `2181` and `9092`.

Access to the Crux service is restricted using the `crux-client-<stack-name>`. Add the `crux-client-<stack-name>`
security group to any service that should be able to connect to Crux. By default, clients should connect to the Crux
service using port `3000`.   

### Environment Variables

| Name                        | Description                                     | Example           |
| --------------------------- | ----------------------------------------------- | ----------------- |
| HEALTH_CHECK_THRESHOLD      | Number of health check failures before shutdown | 3                 |
| HEALTH_CHECK_INTERVAL       | Frequency of health checks (sec)                | 5                 |
| HEALTH_CHECK_WAIT_TIME      | Wait time before starting health checks (sec)   | 5                 |
| STORAGE_DIR                 | Data storage directory                          | `/dev/crrux-data` |
| KAFKA_BOOTSTRAP_SERVER      | Kafka Bootstrap Server Address                  | `localhost:9092`  |
| SERVER_PORT                 | Crux HTTP API Server Port                       | 3000              |

## License

The MIT License (MIT)

Copyright © 2019-2020 callahanwilliam.com

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated 
documentation files (the "Software"), to deal in the Software without restriction, including without limitation the 
rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit 
persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the 
Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE 
WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR  
COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR 
OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

[Crux]:https://github.com/juxt/crux