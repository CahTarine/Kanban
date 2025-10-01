package com.projeto.quadrokanban.core.port.output;

import com.projeto.quadrokanban.core.domain.model.Task;

public interface NotificationOutputPort {
    void notifyUser(Task task);
}
