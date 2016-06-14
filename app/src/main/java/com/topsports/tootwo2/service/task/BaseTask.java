package com.topsports.tootwo2.service.task;

import android.content.Context;

import java.util.Map;

/**
 * 任务通用接口
 * Created by tootwo2 on 16/5/6.
 */
public interface BaseTask {
    public String doTask(Map<String,String> params, Context context);
}
