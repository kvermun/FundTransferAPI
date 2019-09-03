import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;


public class DataStore {

    Map<String, Float> accountBalanceInfo = new HashMap<>();
    Semaphore registrationLock = new Semaphore(1);
    Semaphore fundTransferLock = new Semaphore(1);

    Response RegisterAccount(String accountId, Float startingBalance)
    {
        if(accountId == null || accountId.length()==0)
        {
            return new Response(false, "Failure! AccountId cannot be empty for Registration");
        }

        if(startingBalance < 0)
        {
            return new Response(false, "Starting balance cannot be negative");
        }

        try{
            registrationLock.acquire();

            if(accountBalanceInfo.get(accountId) != null)
            {
                registrationLock.release();
                return new Response(false, "Failure! Account with Id " + accountId + " already registered");
            }

            accountBalanceInfo.put(accountId, startingBalance);
            registrationLock.release();
        }
        catch(InterruptedException e)
        {
            e.printStackTrace();
            if(accountBalanceInfo.get(accountId) != null)
                accountBalanceInfo.remove(accountId);
            return new Response(false, "Please retry registration after some time");
        }

        return new Response(true, "Account with Id" + accountId + " registered successfully");
    }

    Boolean checkIfAccountExists(String accountId)
    {
        if(accountBalanceInfo.get(accountId) == null)
            return false;
        return true;
    }

    Boolean sufficientFundsCheck(String accountId1,  Float amount)
    {
        if( accountBalanceInfo.get(accountId1) - amount < 0)
            return false;

        return true;
    }

    Response transferMoney(String accountId1, String accountId2, Float amount)
    {
        if(amount < 0)
            return new Response(false, "Amount to be transferred cannot be negative");

        if(!checkIfAccountExists(accountId1))
            return new Response(false, "Account "+accountId1+" not registered to transfer funds");

        if(!checkIfAccountExists(accountId2))
            return new Response(false, "Account "+accountId2+" not registered to transfer funds");

        if(!sufficientFundsCheck(accountId1, amount))
            return new Response(false, "Account "+accountId1+" does not sufficient funds. " +
                    "Please add "+ (amount - accountBalanceInfo.get(accountId1)) + " more funds to the account");

        Float balanceBeforeTransactionAc1 = accountBalanceInfo.get(accountId1);
        Float balanceBeforeTransactionAc2 = accountBalanceInfo.get(accountId2);

        try {
            fundTransferLock.acquire();
            accountBalanceInfo.put(accountId1, accountBalanceInfo.get(accountId1) - amount);
            accountBalanceInfo.put(accountId2, accountBalanceInfo.get(accountId2) + amount);
            fundTransferLock.release();
        }
        catch(InterruptedException e)
        {
            accountBalanceInfo.put(accountId1, balanceBeforeTransactionAc1);
            accountBalanceInfo.put(accountId2, balanceBeforeTransactionAc2);
            e.printStackTrace();
            return new Response(false, "Please retry fund transfer after sometime");
        }

        return new Response(true, "Fund transfer Successful");
    }

}
