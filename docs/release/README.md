# Release
Only [@jaredpetersen](https://github.com/jaredpetersen) is able to deploy a new release of Kafka Connect ArangoDB. Always deploy a new version to Maven Central and Confluent Hub.

## GitHub
1. Revise Kafka Connect ArangoDB's version in your pull request before merging.
2. Merge your pull request to master.
3. GitHub CI will automatically build the release, publish to Maven Central staging, and create a new GitHub Release

## Maven Central
1. Log in to the [repository manager](https://oss.sonatype.org/) for Maven Central.
2. Navigate to [Staging Repositories](https://oss.sonatype.org/#stagingRepositories) and locate the staged release in the listings.
3. Click on the staged release and navigate to the Content tab.
4. Confirm that the content is correct. As an additional measure, you can download the published jar and confirm that the JAR works in the [local development cluster](/docs/development).
5. If the content is correct, close the release by clicking the Close button at the top.
6. The repository manager will begin evaluating the releasability of the plugin against the [Maven Central requirements](https://central.sonatype.org/pages/requirements.html). Assuming that everything is good to go, release the plugin by clicking the Release button at the top.
7. Drop the staged repository by clicking the Drop button at the top.
8. Wait at least an hour and [confirm that the plugin has been updated](https://search.maven.org/search?q=g:io.github.jaredpetersen%20AND%20a:kafka-connect-arangodb&core=gav).

## Confluent Hub
1. Pull the latest master branch of the repository.
2. Package the plugin in Confluent Hub's archive format.
    ```
    mvn clean package
    ```
3. Send an email to [confluent-hub@confluent.io](mailto:confluent-hub@confluent.io?Subject=Kafka%20Connect%20ArangoDB%20Plugin%20--%20New%20Version%20Submission) attached with the compressed component archive located at `/target/components/packages`. If the component archive is too large, upload it to a cloud storage provider and provide the link.

TODO: Save the Confluent Hub artifact as a GitHub Actions build artifact so that we can download it instead of building twice
