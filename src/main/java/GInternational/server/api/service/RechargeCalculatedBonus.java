package GInternational.server.api.service;

public class RechargeCalculatedBonus {

    public static int calculateBonus(int userLevel, double rechargeAmount) {
        double bonusPercentage = getBonusPercentage(userLevel);
        double calculatedBonus = rechargeAmount * bonusPercentage;
        return (int) Math.round(calculatedBonus);
    }

    private static double getBonusPercentage(int userLevel) {
        switch (userLevel) {
            case 1:
            case 2:
                return 0.05;
            case 3:
            case 4:
                return 0.06;
            case 5:
            case 6:
                return 0.07;
            case 7:
                return 0.08;
            case 8:
                return 0.0; // 0% bonus
            case 9:
                return 0.03;
            case 10:
                return 0.10; // VIP level
            default:
                return 0.0;
        }
    }
}
