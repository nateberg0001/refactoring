package theater;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

/**
 * This class generates a statement for a given invoice of performances.
 */
public class StatementPrinter {
    public Invoice invoice;
    public Map<String, Play> plays;

    public StatementPrinter(Invoice invoice, Map<String, Play> plays) {
        this.invoice = invoice;
        this.plays = plays;
    }

    /**
     * Returns a formatted statement of the invoice associated with this printer.
     * @return the formatted statement
     * @throws RuntimeException if one of the play types is not known
     */
    public String statement() {
        int totalAmount = 0;
        int volumeCredits = 0;
        StringBuilder result = new StringBuilder("Statement for " + invoice.getCustomer() + System.lineSeparator());
        volumeCredits = getVolumeCredits();
        totalAmount = getTotalAmount(result);
        result.append(String.format("Amount owed is %s%n",
                usd(totalAmount)));
        result.append(String.format("You earned %s credits%n", volumeCredits));
        return result.toString();
    }

    private int getVolumeCredits() {
        int rslt = 0;
        for (Performance performance : invoice.getPerformances()) {
            Play play = plays.get(performance.playID);
            rslt += getVolumeCredits(performance, play);
        }
        return rslt;
    }

    private int getTotalAmount(StringBuilder result) {
        int rslt = 0;
        for (Performance performance : invoice.getPerformances()) {
            Play play = plays.get(performance.playID);
            int amount = getAmount(performance);
            // print line for this order
            result.append(String.format("  %s: %s (%s seats)%n", play.name,
                    usd(amount), performance.audience));
            rslt += amount;
        }
        return rslt;
    }

    private static String usd(int rslt) {
        return NumberFormat.getCurrencyInstance(Locale.US).format(rslt / 100);
    }

    private Play getPlay(Performance performance){
        return this.plays.get(performance.playID);
    }

    private static int getVolumeCredits(Performance performance, Play play) {
        int result = 0;
        // add volume credits
        result += Math.max(performance.audience - Constants.BASE_VOLUME_CREDIT_THRESHOLD, 0);
        // add extra credit for every five comedy attendees
        if ("comedy".equals(play.type)) result += performance.audience /
                Constants.COMEDY_EXTRA_VOLUME_FACTOR;
        return result;
    }

    private int getAmount(Performance p) {
        int thisAmount;
        switch (this.getPlay(p).type) {
            case "tragedy":
                thisAmount = 40000;
                if (p.audience > Constants.TRAGEDY_AUDIENCE_THRESHOLD) {
                    thisAmount += 1000 * (p.audience - 30);
                }
                break;
            case "comedy":
                thisAmount = Constants.COMEDY_BASE_AMOUNT;
                if (p.audience > Constants.COMEDY_AUDIENCE_THRESHOLD) {
                    thisAmount += Constants.COMEDY_OVER_BASE_CAPACITY_AMOUNT
                            + (Constants.COMEDY_OVER_BASE_CAPACITY_PER_PERSON
                            * (p.audience - Constants.COMEDY_AUDIENCE_THRESHOLD));
                }
                thisAmount += Constants.COMEDY_AMOUNT_PER_AUDIENCE * p.audience;
                break;
            default:
                throw new RuntimeException(String.format("unknown type: %s", p.playID));
        }
        return thisAmount;
    }
}
