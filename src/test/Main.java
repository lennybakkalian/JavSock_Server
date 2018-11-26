package test;

import javsock.JavSock;

public class Main {

	public static void main(String[] args) {
		try {
			JavSock js = new JavSock(2222);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
