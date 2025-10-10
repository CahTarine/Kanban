package com.projeto.quadrokanban.adapter.output.notification;

import com.projeto.quadrokanban.core.domain.model.Task;
import com.projeto.quadrokanban.core.port.output.NotificationOutputPort;
import com.projeto.quadrokanban.util.colors.Colors;
import org.springframework.stereotype.Component;

@Component
public class ConsoleNotificationAdapter implements NotificationOutputPort {

    @Override
    public void notifyUser(Task task){
        System.out.println(Colors.TEXT_BLUE + Colors.ANSI_BLACK_BACKGROUND +
                "------------------------------------");
        System.out.println("--- NEW NOTIFICATION EVENT ---");
        System.out.println("Task assigned:");
        System.out.println("Title: " + task.getTitle());
        System.out.println("User ID: " + task.getUserId());
        System.out.println("------------------------------------");
        System.out.println("                                                     " + Colors.TEXT_RESET);
    }
}
