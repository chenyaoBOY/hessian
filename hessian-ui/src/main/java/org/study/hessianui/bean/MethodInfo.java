package org.study.hessianui.bean;

/**
 * @author chenyao
 * @date 2019/6/12 10:45
 * @description
 */
public class MethodInfo {

    private String methodName;
    private Integer num;

    public MethodInfo(String methodName, Integer num) {
        this.methodName = methodName;
        this.num = num;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }
}
