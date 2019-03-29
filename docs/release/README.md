# Release
Only [@jaredpetersen](https://github.com/jaredpetersen) is able to deploy a new release of Kafka Connect ArangoDB. Always deploy a new version to Maven Central and Confluent Hub.

## GitHub
1. Ensure that Kafka Connect ArangoDB's version was revised in the last merge to the master branch.
2. Go to the [Releases section](https://github.com/jaredpetersen/kafka-connect-arangodb/releases) of the repository on GitHub.
3. Draft a new release
4. Fill the tag version input box with the new release version.
5. Fill the release title input box with the new release version.
6. Fill in the release description input bux with the recent changes using the [Keep a Changelog standard](https://keepachangelog.com/en/1.0.0/).
7. Click the Publish release button.

## Maven Central
1. Ensure that Kafka Connect ArangoDB's version was revised in the last merge to the master branch.
2. Deploy the plugin to Maven Central for staging.
    ```
    mvn clean deploy -Pdeploy
    ```
3. Log in to the [repository manager](https://oss.sonatype.org/) for Maven Central.
4. Navigate to [Staging Repositories](https://oss.sonatype.org/#stagingRepositories) and locate the staged release in the listings.
5. Click on the staged release and navigate to the Content tab.
6. Confirm that the content is correct. As an additional measure, you can download the published jar and confirm that the JAR works in the [local development cluster](/docs/development).
7. If the content is correct, close the release by clicking the Close button at the top.
8. The repository manager will begin evaluating the releasability of the plugin against the [Maven Central requirements](https://central.sonatype.org/pages/requirements.html). Assuming that everything is good to go, release the plugin by clicking the Release button at the top.
9. Drop the staged repository by clicking the Drop button at the top.
10. Wait at least an hour and [confirm that the plugin has been updated](https://search.maven.org/search?q=g:io.github.jaredpetersen%20AND%20a:kafka-connect-arangodb&core=gav).

## Confluent Hub
1. Ensure that Kafka Connect ArangoDB's version was revised in the last merge to the master branch.
2. Package the plugin in Confluent Hub's archive format.
    ```
    mvn clean package
    ```
3. Send an email to [confluent-hub@confluent.io](mailto:confluent-hub@confluent.io?Subject=Kafka%20Connect%20ArangoDB%20Plugin%20--%20New%20Version%20Submission) attached with the compressed component archive located at `/target/components/packages`. If the component archive is too large, upload it to a cloud storage provider and provide the link.