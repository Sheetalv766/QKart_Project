package QKART_TESTNG;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;


public class ListenerClass implements ITestListener {

    @Override
    public void onStart(ITestContext context){
        System.out.println("***************TestCase Start***************");
    }

    @Override
    public void onTestStart(ITestResult result) {
        System.out.println("TestCase Start : " + result.getName() + " Taking Screenshot !");
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        System.out.println("TestCase Success : " + result.getName() + " Taking Screenshot !");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        System.out.println("TestCase Failed : " + result.getName() + " Taking Screenshot !");
    }

    public void onFinish(ITestContext context) {
    System.out.println("***************TestCase End***************");
    }
}
