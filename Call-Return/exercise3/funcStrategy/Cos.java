package funcStrategy;
public class Cos implements FuncStrategy {
    @Override
    public double execute(double number) {
        return Math.cos(number);
    }

    @Override
    public String getName() {
        return "Cos";
    }
}
