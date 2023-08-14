package com.github.fanzezhen.common.core;

import lombok.extern.slf4j.Slf4j;
import org.junit.Ignore;

/**
 * @author zezhen.fan
 * @date 2023/8/10
 */

@Slf4j
@Ignore
public class ReverseLinkedListTest {
    static class ListNode {
        int val;
        ListNode next;

        ListNode(int val) {
            this.val = val;
        }
    }

    static class ReverseLinkedList {
        public ListNode reverse(ListNode head) {
            ListNode prev = null;
            ListNode current = head;
            ListNode next = null;

            while (current != null) {
                next = current.next; // 保存下一个节点的引用
                current.next = prev; // 当前节点指向前一个节点
                prev = current; // 更新prev为当前节点
                current = next; // 更新current为下一个节点
            }

            return prev; // 返回逆转后的头节点
        }

        public static void main(String[] args) {
            ReverseLinkedList reverseLinkedList = new ReverseLinkedList();

            // 创建一个链表：1 -> 2 -> 3 -> 4 -> 5
            ListNode head = new ListNode(1);
            ListNode node2 = new ListNode(2);
            ListNode node3 = new ListNode(3);
            ListNode node4 = new ListNode(4);
            ListNode node5 = new ListNode(5);
            head.next = node2;
            node2.next = node3;
            node3.next = node4;
            node4.next = node5;

            // 逆转链表
            ListNode reversedHead = reverseLinkedList.reverse(head);

            // 打印逆转后的链表：5 -> 4 -> 3 -> 2 -> 1
            ListNode currentNode = reversedHead;
            while (currentNode != null) {
                System.out.print(currentNode.val + " -> ");
                currentNode = currentNode.next;
            }
            System.out.print("null");
        }
    }
}
