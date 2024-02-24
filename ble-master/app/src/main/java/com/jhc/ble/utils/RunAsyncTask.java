package com.jhc.ble.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * @author：Chao
 * @date：2023/8/25
 */

public class RunAsyncTask extends AsyncTask<Integer,Integer,Integer> {
    private final String TAG="TestAsyncTask";
    private TextView textView;
    private ProgressBar progressBar;
    private Context context;

    public RunAsyncTask(TextView textView, ProgressBar progressBar, Context context) {
        this.textView = textView;
        this.progressBar = progressBar;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.d(TAG,"方法_______onPreExecute()_______执行所在线程_________"+Thread.currentThread().getName()+"______________");
    }

    @Override
    protected Integer doInBackground(Integer... ints) {
        Integer count = ints[0];
        while (count<50&&!isCancelled()){//isCancelled()表示判断当前任务是否被取消，防止在取消异步任务的时候循环不能及时停下
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            count++;
            Log.d(TAG,"方法_______doInBackground()_______执行所在线程_________"+Thread.currentThread().getName()+"_______"+count+"_______");
            publishProgress(count);
        }
        return count;
    }

    @Override
    protected void onPostExecute(Integer i) {
//        任务执行onCancelled()回调的时候不会执行当前onPostExecute()方法
        Log.d(TAG,"方法_______onPostExecute()_______执行所在线程_________"+Thread.currentThread().getName()+"______________");
        textView.setText(i+"");
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        Log.d(TAG,"方法_______onProgressUpdate()_______执行所在线程_________"+Thread.currentThread().getName()+"______________");
        textView.setText(values[0]+"");
        progressBar.setProgress(values[0]);
    }

    @Override
    protected void onCancelled() {
        Log.d(TAG,"方法_______onCancelled()_______执行所在线程_________"+Thread.currentThread().getName()+"______________");
        super.onCancelled();
        //Toast.makeText(context,"任务取消成功",Toast.LENGTH_LONG).show();
    }
}
