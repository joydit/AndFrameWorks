package com.andframe.api.task;

/**
 * 任务
 * Created by SCWANG on 2016/10/13.
 */

public interface TaskExceptionHandler {

    void onTaskException(Throwable throwable);

}
