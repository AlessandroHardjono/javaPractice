package ui;

import Budget_stuff.AccountControl;
import Budget_stuff.BeginnerFinance;
import Budget_stuff.ExpertFinance;
import Budget_stuff.FinancePlan;
import exceptions.NegativeNumberException;

import java.util.Scanner;
import java.io.IOException;
import java.util.List;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.*;
import java.awt.FlowLayout;
import java.awt.event.*;

import org.json.JSONObject;


public class FinancePlanner extends JFrame {
    Scanner scanner = new Scanner(System.in);
    private JTextField nameField;
    private JTextField boeField;
    private JTextField addField;
    private JTextField spendField;

    public FinancePlanner() throws IOException, NegativeNumberException {
        super("The Finance Planner 5000");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(350,350);
        setVisible(true);
        setLayout(new FlowLayout());
        AccountControl currentUser = new AccountControl("", 0);

        int financeType;

        List<String> lines = Files.readAllLines(Paths.get("outputFile.txt"));
        PrintWriter writer = new PrintWriter("outputFile.txt", "UTF-8");


        Float balance = Float.parseFloat(lines.get(0));

        while (true) {
            //AccountControl currentUser = new AccountControl();

            currentUser.stateName();
            nameField = new JTextField(10);
            add(nameField);
            String name = JOptionPane.showInputDialog("State your name: ");
//                    scanner.nextLine();
            currentUser.establishUser(name);
            currentUser.addUser(name);


            while (true) {
                try {
//                    System.out.println("Choose a type (0)Beginner (1)Expert");
                    String choiceF = JOptionPane.showInputDialog("Choose a type (0)Beginner (1) Expert");
                    financeType = Integer.parseInt(choiceF);
//                            scanner.nextInt();
                    break;
                } catch (java.util.InputMismatchException e) {
                    System.out.println("Please try again.");
                    String choiceF = JOptionPane.showInputDialog("Choose a type (0)Beginner (1) Expert");
//                    financeType = Integer.parseInt(choiceF);
//                    scanner.nextLine();
                }
            }

            if (financeType == 0) {
                beginnerFinanceAction(currentUser, financeType, writer, balance);
            } else if (financeType == 1) {
                expertFinanceAction(currentUser, financeType, writer, balance);
            }

            JLabel accounts = new JLabel("Accounts so far: "+ currentUser.displayID());
            accounts.setToolTipText("This is the amount of accounts active right now.");
//            System.out.println("Accounts so far:");
//            System.out.println(currentUser.displayID());

            System.out.println("try again? (0)Yes (1)No (2) Remove name");
            int tryAgain = scanner.nextInt();
            if (tryAgain == 1) { break; }
            else if (tryAgain == 0) {
                currentUser.stateName();
                name = scanner.nextLine();
                currentUser.establishUser(name);
                currentUser.addUser(name);
            }
            else if (tryAgain == 2) {
                currentUser.retID().remove(currentUser);
            }

        }

        System.out.println("goodbye!");

        return;


    }

    private void expertFinanceAction(AccountControl currentUser, int financeType, PrintWriter writer, Float balance) throws NegativeNumberException, IOException {
        currentUser.retID().put(currentUser, "Expert");
        currentUser.addType(financeType);

        FinancePlan expertFinance_plan = new ExpertFinance();
        expertFinance_plan.addBalance(balance);
        financeAction(expertFinance_plan);
        writer.println(Float.toString(expertFinance_plan.retBalance()));
        writer.close();
    }

    private void beginnerFinanceAction(AccountControl currentUser, int financeType, PrintWriter writer, Float balance) throws NegativeNumberException, IOException {
        currentUser.retID().put(currentUser, "Beginner");
        currentUser.addType(financeType);

        FinancePlan beginnerFinance_plan = new BeginnerFinance();
        currentUser. addObserver(beginnerFinance_plan);

        beginnerFinance_plan.addBalance(balance);
        financeAction(beginnerFinance_plan);
        writer.println(Float.toString(beginnerFinance_plan.retBalance()));
        writer.close();
    }


    private void financeAction(FinancePlan f) throws NegativeNumberException, IOException {
        int action;
        f.beginBudget();
        f.fromZero();
        while (true) {
            f.enterStartPlan();
            while (true) {
                try {
                    action = scanner.nextInt();
                    break;
                } catch (java.util.InputMismatchException e) {
                    System.out.println("Please try again.");
                    scanner.nextLine();
                } finally {
                    System.out.println("Input a number.");
                }
            }

            if (action == 1) {
                addBalanceToPlan(f);
            } else if (action == 2) {
                spendBalanceFromPlan(f);
            } else if (action == 3) {
                break;
            } else if (action == 4) {
                displayWeb();
            }
        }


    }

    private void spendBalanceFromPlan(FinancePlan f) throws NegativeNumberException {
        float spending;
        System.out.println("Enter amount of spending");
        while (true) {
            try {
                spending = scanner.nextFloat();
                break;
            } catch (java.util.InputMismatchException e) {
                System.out.println("Please try again.");
                scanner.nextLine();
            }
        }

        if (spending > f.retBalance()) {
            throw new NegativeNumberException("Game Over.");
        } else {
            f.subSpending(spending);
            System.out.println("Balance is now: " + f.retBalance());
        }
    }

    private void addBalanceToPlan(FinancePlan f) throws NegativeNumberException {
        float amount;
        System.out.println("Enter amount of money to your balance");
        while (true) {
            try {
                amount = scanner.nextFloat();
                break;
            } catch (java.util.InputMismatchException e) {
                System.out.println("Please try again.");
                scanner.nextLine();
            }
        }
        if (amount >= 100) {
            f.addBalance(amount);
            f.compliment();
        } else if (amount < 0) {
            throw new NegativeNumberException("Game Over.");
        } else {
            f.addBalance(amount);
            System.out.println("Balance is now: " + f.retBalance());
        }
    }

    private void displayWeb() throws IOException, MalformedURLException {

        BufferedReader br = null;

        try {
            String apiKey = "6aed9371bcf68da6cee41dfebf8de257";
            String weatherQuerySydney = "http://data.fixer.io/api/latest?access_key=";
            String actualURL = weatherQuerySydney+apiKey;

            URL url = new URL(actualURL);
            br = new BufferedReader(new InputStreamReader(url.openStream()));

            String line;

            StringBuilder sb = new StringBuilder();

            while ((line = br.readLine()) != null) {

                sb.append(line);
                sb.append(System.lineSeparator());
            }
            System.out.println(sb.toString());
            JSONObject timestamp = new JSONObject(sb.toString());
            int tS = timestamp.getInt("timestamp");
            System.out.println("Timestampe is: " + tS);

        } finally {

            if (br != null) {
                br.close();
            }
        }
    }

    public static void main(String[] args) throws IOException, NegativeNumberException {
        FinancePlanner fplay = new FinancePlanner();
    }

}
