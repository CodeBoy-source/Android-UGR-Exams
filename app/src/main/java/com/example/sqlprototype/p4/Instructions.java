package com.example.sqlprototype.p4;

public class Instructions {
    String instructions;
    String nextNodeName;
    int nextNode;

    Instructions(String instructions, String nextNodeName, int nextNode) {
        this.instructions = instructions;
        this.nextNodeName = nextNodeName;
        this.nextNode = nextNode;
    }
}
