package name.abuchen.portfolio.model;

import java.io.Serializable;
import java.util.Comparator;

/**
 * A pair of transaction owner (account or portfolio) and a transaction.
 */
public class TransactionPair<T extends Transaction>
{
    public static final class ByDate implements Comparator<TransactionPair<?>>, Serializable
    {
        private static final long serialVersionUID = 1L;

        @Override
        public int compare(TransactionPair<?> t1, TransactionPair<?> t2)
        {
            return t1.getTransaction().getDate().compareTo(t2.getTransaction().getDate());
        }
    }

    private final TransactionOwner<T> owner;
    private final T transaction;

    public TransactionPair(TransactionOwner<T> owner, T transaction)
    {
        this.owner = owner;
        this.transaction = transaction;
    }

    public TransactionOwner<T> getOwner()
    {
        return owner;
    }

    public T getTransaction()
    {
        return transaction;
    }
}
