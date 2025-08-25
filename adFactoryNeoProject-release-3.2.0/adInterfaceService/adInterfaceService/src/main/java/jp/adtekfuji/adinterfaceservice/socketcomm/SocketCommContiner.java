/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adinterfaceservice.socketcomm;

import io.netty.channel.Channel;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import jp.adtekfuji.adinterfaceservicecommon.plugin.SocketServerHandlerInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author ke.yokoi
 */
public final class SocketCommContiner extends Thread {

    private static final Logger logger = LogManager.getLogger();

    /**
     *
     */
    class RecvCommand {

        Channel channel;
        Object command;

        public RecvCommand(Channel channel, Object command) {
            this.channel = channel;
            this.command = command;
        }

    }

    private final SocketServerHandlerInterface handler;
    private final List<Class> commandPool;
    private final LinkedList<RecvCommand> recvQueue = new LinkedList<>();
    private Boolean threaded = false;

    /**
     *
     * @param handler
     */
    public SocketCommContiner(SocketServerHandlerInterface handler) {
        this.handler = handler;
        this.commandPool = new ArrayList(handler.getCommandCollection());
    }

    /**
     *
     */
    public void startHandler() {
        threaded = true;
        start();
        handler.startHandler();
    }

    /**
     *
     */
    public void stopHandler() {
        try {
            threaded = false;
            synchronized (recvQueue) {
                recvQueue.notify();
            }
            handler.stopHandler();
            interrupt();
            join();
        } catch (InterruptedException ex) {
        }
    }

    /**
     *
     * @param channel
     */
    public void connectChannel(Channel channel) {
        handler.connectChannel(channel);
    }

    /**
     *
     * @param channel
     */
    public void disconnectChannel(Channel channel) {
        handler.disconnectChannel(channel);
    }

    /**
     *
     */
    @Override
    public void run() {
        try {
            logger.info("Messaging thread start.");

            RecvCommand recvCommand;

            while (threaded) {
                try {
                    recvCommand = null;
                    synchronized (recvQueue) {
                        if (recvQueue.isEmpty()) {
                            try {
                                recvQueue.wait();
                            } catch (InterruptedException ex) {
                            }
                        }

                        if (!recvQueue.isEmpty()) {
                            recvCommand = recvQueue.removeFirst();
                        }

                        if (Objects.nonNull(recvCommand)) {
                            logger.info("Messaging : " + recvCommand.command);
                            handler.recvCommand(recvCommand.channel, recvCommand.command);
                        }
                    }
                } catch (Exception ex) {
                    logger.fatal(ex);
                }
            }

            threaded = false;
        } finally {
            logger.info("Messaging thread end.");
        }
    }

    /**
     *
     * @param channel
     * @param command
     */
    public void acceptRecv(Channel channel, Object command) {
        logger.info("Recv : " + command);
        if (commandPool.contains(command.getClass())) {
            synchronized (recvQueue) {
                recvQueue.add(new RecvCommand(channel, command));
                recvQueue.notify();
            }
        }
    }

    @Override
    public String toString() {
        return new StringBuilder("SocketCommContiner{")
                .append("handler=").append(this.handler)
                .append("}")
                .toString();
    }
}
