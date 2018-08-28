package command;

import at.rgstoettner.alexahome.plugin.Device;

public class Hello implements Device {


    public static void main(String[] args) {
        Device d = new Hello();
        d.run("hello world");
    }


    @Override
    public void run(String argument) {
        System.out.println(argument);
    }
}
