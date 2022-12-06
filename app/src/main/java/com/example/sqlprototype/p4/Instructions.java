package com.example.sqlprototype.p4;

public class Instructions {
    String instructions;
    String nextNodeName;
    int nextNode;
    String direction;

    Instructions(String instructions, String nextNodeName, int nextNode, String direction) {
        this.instructions = instructions;
        this.nextNodeName = nextNodeName;
        this.nextNode = nextNode;
        this.direction = direction;
    }
}
