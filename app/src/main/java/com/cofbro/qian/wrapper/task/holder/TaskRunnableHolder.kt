package com.cofbro.qian.wrapper.task.holder

import java.util.concurrent.Executors
import java.util.concurrent.Future


/**
 * @author chy 2023.12.19
 */
class TaskRunnableHolder {
    private val workTasks = arrayListOf<Task>()
    private var resultFuture: List<Future<Result?>> = arrayListOf()
    private val result = arrayListOf<Result?>()
    private val threadWorker = Executors.newScheduledThreadPool(6)

    private constructor()
    constructor(tasks: List<Task>) : this() {
        workTasks.addAll(tasks)
    }

    fun submit(): List<Future<Result?>> {
        resultFuture = threadWorker.invokeAll(workTasks)
        return resultFuture
    }

    fun blockingFetchingResult(): List<Result?> {
        resultFuture.forEach {
            result.add(it.get())
        }
        threadWorker.shutdown()
        return result
    }
}