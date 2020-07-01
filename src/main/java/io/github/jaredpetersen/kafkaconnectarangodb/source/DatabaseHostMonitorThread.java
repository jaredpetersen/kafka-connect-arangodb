package io.github.jaredpetersen.kafkaconnectarangodb.source;

import org.apache.kafka.connect.connector.ConnectorContext;
import org.apache.kafka.connect.errors.ConnectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class DatabaseHostMonitorThread extends Thread {
  private final ConnectorContext context;
  private final Long pollInterval;
  private final String host;
  private final CountDownLatch shutdownLatch;
  private Set<String> savedAddresses = Collections.emptySet();

  private static final long TIMEOUT = 1000L;

  private static final Logger LOG = LoggerFactory.getLogger(DatabaseHostMonitorThread.class);

  public DatabaseHostMonitorThread(ConnectorContext context, long pollInterval, String host) {
    this.context = context;
    this.host = host;
    this.pollInterval = pollInterval;
    this.shutdownLatch = new CountDownLatch(1);
  }

  @Override
  public void run() {
    while (shutdownLatch.getCount() > 0) {
      LOG.info("retrieving database addresses");
      Set<String> foundAddresses = getDatabaseAddresses(this.host);

      if (!this.savedAddresses.containsAll(foundAddresses)) {
        this.savedAddresses = foundAddresses;
        this.context.requestTaskReconfiguration();
      }

      try {
        LOG.debug("waiting {}ms to check for changed database addresses", pollInterval);
        boolean shuttingDown = shutdownLatch.await(pollInterval, TimeUnit.MILLISECONDS);

        if (shuttingDown) {
          return;
        }
      } catch (InterruptedException exception) {
        LOG.warn("host monitor interrupted: ", exception);
      }
    }
  }

  public synchronized Set<String> getDatabaseAddresses() {
    while (this.savedAddresses.isEmpty()) {
      try {
        wait(TIMEOUT);
      } catch (InterruptedException e) {
        // Ignore
      }
    }
    if (this.savedAddresses.isEmpty()) {
      throw new ConnectException("failed to get database addresses in time");
    }
    return this.savedAddresses;
  }

  public void shutdown() {
    LOG.info("shutting down database host monitoring thread");
    this.shutdownLatch.countDown();
  }

  private Set<String> getDatabaseAddresses(final String host) {
    InetAddress[] foundAddresses;

    // Get IP addresses for host
    try {
      foundAddresses = InetAddress.getAllByName(host);
      LOG.info("found database servers " + Arrays.toString(foundAddresses));
    } catch (UnknownHostException exception) {
      LOG.error("host does not exist", exception);
      foundAddresses = new InetAddress[]{};
    }

    return Arrays.stream(foundAddresses)
        .map(InetAddress::getHostAddress)
        .collect(Collectors.toSet());
  }
}
