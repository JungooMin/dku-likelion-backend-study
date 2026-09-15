package com.ll.demo01;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.awt.*;

@Controller
public class Home2Controller {

    @Autowired
    private ComponentA componentA;

    public Home2Controller(){
        componentA = new ComponentA();
    }
    public void action(){
        componentA.action();
    }
}
