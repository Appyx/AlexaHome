package com.example.command;

import at.rgstoettner.alexahome.plugin.Device;

public class Command implements Device {
    @Override
    public void run() {
        System.out.println("hello from jar plugin");
    }
}
