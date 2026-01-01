package org.sri.moviebooking;
import java.util.Scanner;

public class CLI {

    public static void runner(String SECRET_KEY){

        Scanner sc = new Scanner(System.in);
        AESEncryption aes = new AESEncryption();

        int choice = 1;
        while(choice!=0){
            System.out.println("1 Write, 2 Read, 0 Exit");
            choice = sc.nextInt();

            switch(choice){
                case 1:
                    break;
                case 2:
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Wrong choice");
            }
        }
    }
}
