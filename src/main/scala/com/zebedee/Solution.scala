package com.zebedee

/**
  *
  * Created by shaodi.chen on 2019/5/23. 
  *
  */

object Solution {

    def main(args: Array[String]): Unit = {
        val listNode1: ListNode = new ListNode(2)
        listNode1.next = new ListNode(4)
        listNode1.next.next = new ListNode(3)
        val listNode2: ListNode = new ListNode(5)
        listNode2.next = new ListNode(6)
        listNode2.next.next = new ListNode(4)
        val getNode: ListNode = addTwoNumbers(listNode1, listNode2)
        print(getNode.x)
    }

    def addTwoNumbers(l1: ListNode, l2: ListNode): ListNode = {
        if (l1 == null || l2 == null) {
            return if (l1 == null) l2 else l1
        } else {
            val outListNode: ListNode = new ListNode((l1.x + l2.x) % 10)
            l1.next.x = l1.next.x + ((l1.x + l2.x) / 10)
            outListNode.next = addTwoNumbers(l1.next, l2.next)
            outListNode
        }
    }
}

class ListNode(var _x: Int = 0) {
    var next: ListNode = null
    var x   : Int      = _x
}




