package org.sri.passmanager;

import java.util.Scanner;
import java.nio.file.Path;

public class CLI {

    public void runner(String SECRET_KEY, Path filePath){

        Scanner sc = new Scanner(System.in);
        AESEncryption aes = new AESEncryption();
        StorageHandler storageHandler = new StorageHandler();

        int choice = 1;
        while(choice!=0){
            System.out.println("1 Write\n2 Read\n0 Exit");
            choice = sc.nextInt();

            switch(choice){
                case 1:
                    storageHandler.storeToFile(SECRET_KEY,filePath);
                    break;
                case 2:
                    storageHandler.readFile(filePath);
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Wrong choice");
            }
        }
    }
}
