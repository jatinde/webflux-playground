package com.github.jatinde.webflux_payground.config.h2;

import org.springframework.context.event.EventListener;

//@Configuration
//@Profile("local") //change or remove according to your convenience
public class H2ConsoleConfiguration {

  private org.h2.tools.Server webServer;

  private org.h2.tools.Server tcpServer;

  private final String WEB_PORT = "8082";
  private final String TCP_PORT = "9092";

  @EventListener(org.springframework.context.event.ContextRefreshedEvent.class)
  public void start() throws java.sql.SQLException {

    this.webServer = org.h2.tools.Server.createWebServer("-webPort", WEB_PORT, "-tcpAllowOthers").start();
    this.tcpServer = org.h2.tools.Server.createTcpServer("-tcpPort", TCP_PORT, "-tcpAllowOthers").start();
  }

  @EventListener(org.springframework.context.event.ContextClosedEvent.class)
  public void stop() {
    this.tcpServer.stop();
    this.webServer.stop();
  }

}
