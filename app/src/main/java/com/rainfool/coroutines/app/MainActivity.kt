package com.rainfool.coroutines.app

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.rainfool.coroutine.CoroutineResult

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    private val netRepositroy = NetRepositroy()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestWithCallback()

        lifecycleScope.launchWhenCreated {
            requestWithCoroutines()
        }
    }

    private fun requestWithCallback() {
        NetRepositroy.callAsyncStatic(DefaultResultListener("callAsyncStatic"))
        NetRepositroy.callAsyncStaticError(DefaultResultListener("callAsyncStaticError"))
        netRepositroy.callAsync(DefaultResultListener("callAsync"))
        netRepositroy.callAsyncError(DefaultResultListener("callAsyncError"))
    }

    private suspend fun requestWithCoroutines() {
        NetRepositroyCoroutine.callAsyncStatic().apply {
            Log.d(TAG, "callAsyncStatic result:${this.data}")
        }
        NetRepositroyCoroutine.callAsyncStaticError().apply {
            Log.e(TAG, "callAsyncStaticError errror code:$errorCode,error msg:$errorMsg")
        }
        netRepositroy.callAsync().apply {
            Log.d(TAG, "callAsync result:${this.data}")
        }
        netRepositroy.callAsyncError().apply {
            Log.e(TAG, "callAsyncStaticError errror code:$errorCode,error msg:$errorMsg")
        }
    }

    private fun logCorotinesResult(result: CoroutineResult<String>) {
    }

    class DefaultResultListener(private val prefix: String) : IResultListener<String> {

        override fun onSuccess(obj: String?) {
            Log.d(TAG, "success :$obj")
        }

        override fun onError(errorCode: Int, errorMsg: String?) {
            Log.e(TAG, "error code:$errorCode,error msg:$errorMsg")
        }

    }
}