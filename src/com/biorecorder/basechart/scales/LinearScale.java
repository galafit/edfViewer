package com.biorecorder.basechart.scales;

import com.biorecorder.basechart.axis.TickFormatInfo;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.Arrays;

/**
 * Created by galafit on 6/9/17.
 */
public class LinearScale implements Scale {
    DecimalFormat numberFormatter;
    private double domain[] = {0, 1};
    private double range[] = {0, 1};

    @Override
    public Scale copy() {
        LinearScale copyScale = new LinearScale();
        copyScale.domain = Arrays.copyOf(domain, domain.length);
        copyScale.range = Arrays.copyOf(range, range.length);
        return copyScale;
    }

    @Override
    public void setDomain(double... domain) {
        this.domain = Arrays.copyOf(domain, domain.length);
        numberFormatter = null;
    }

    @Override
    public void setRange(double... range) {
        this.range = Arrays.copyOf(range, range.length);
        numberFormatter = null;
    }

    @Override
    public double[] getDomain() {
        return Arrays.copyOf(domain, domain.length);
    }

    @Override
    public double[] getRange() {
        return Arrays.copyOf(range, range.length);
    }

    @Override
    public double scale(double value) {
        return (float) (range[0] + (value - domain[0]) * (range[range.length - 1] - range[0]) / (domain[domain.length - 1] - domain[0]));
    }

    @Override
    public double invert(double value) {
        return domain[0] + (value - range[0]) * (domain[domain.length - 1] - domain[0]) / (range[range.length - 1] - range[0]);
    }

    @Override
    public TickProvider getTickProviderByIntervalCount(int tickIntervalCount, TickFormatInfo formatInfo) {
        LinearTickProvider provider = new LinearTickProvider(formatInfo);
        provider.setTickIntervalCount(tickIntervalCount);
        return provider;
    }

    @Override
    public TickProvider getTickProviderByInterval(double tickInterval, TickFormatInfo formatInfo) {
        LinearTickProvider provider = new LinearTickProvider(formatInfo);
        provider.setTickInterval(tickInterval);
        return provider;
    }

    @Override
    public String formatDomainValue(double value) {
        if (numberFormatter == null) {
            float rangePointsCount = (int)Math.abs(range[range.length - 1] - range[0]) + 1;
            double domainLength = domain[domain.length - 1] - domain[0];
            NormalizedNumber pointInterval = new NormalizedNumber(domainLength / rangePointsCount);
            numberFormatter = getNumberFormat(pointInterval.getPower(), null);
        }
        long longPart = (long) value;
        if(value == longPart) {
            return String.valueOf(longPart);
        }
        return numberFormatter.format(value);
    }

    // TODO: use metric shortcuts - k, M, G... from formatInfo
    private DecimalFormat getNumberFormat(int power, TickFormatInfo labelFormatInfo) {
        DecimalFormat dfNeg4 = new DecimalFormat("0.0000");
        DecimalFormat dfNeg3 = new DecimalFormat("0.000");
        DecimalFormat dfNeg2 = new DecimalFormat("0.00");
        DecimalFormat dfNeg1 = new DecimalFormat("0.0");
        DecimalFormat df0 = new DecimalFormat("#,##0");

        DecimalFormat df = new DecimalFormat("#.######E0");

        if (power == -4) {
            df = dfNeg4;
        }
        if (power == -3) {
            df = dfNeg3;
        }
        if (power == -2) {
            df = dfNeg2;
        }
        if (power == -1) {
            df = dfNeg1;
        }
        if (power >= 0 && power <= 6) {
            df = df0;
        }

        String formatPattern = df.toPattern();
        if (labelFormatInfo != null && labelFormatInfo.getPrefix() != null) {
            formatPattern = labelFormatInfo.getPrefix() + " " + formatPattern;
        }
        if (labelFormatInfo != null && labelFormatInfo.getSuffix() != null) {
            formatPattern = formatPattern + " " + labelFormatInfo.getSuffix();

        }
        df = new DecimalFormat(formatPattern);
        return df;
    }


    class LinearTickProvider implements TickProvider {
        private double tickInterval = 1;
        int tickDigits;
        int tickPower;
        private TickFormatInfo labelFormatInfo;
        private DecimalFormat labelFormat = new DecimalFormat();
        private Tick lastTick;

        public LinearTickProvider(TickFormatInfo labelFormatInfo) {
            this.labelFormatInfo = labelFormatInfo;
        }

        public void setTickInterval(double tickInterval) {
            this.tickInterval = tickInterval;
            NormalizedNumber normalizedInterval = new NormalizedNumber(tickInterval);
            tickPower = normalizedInterval.getPowerOfLastSignificantDigit();
            tickDigits = (int) (normalizedInterval.getMantissa() * Math.pow(10, tickPower));
            labelFormat = getNumberFormat(tickPower, labelFormatInfo);
        }

        public void setTickIntervalCount(int tickIntervalCount) {
            NormalizedNumber normalizedInterval = getRoundTickInterval(tickIntervalCount);
            tickInterval = normalizedInterval.getValue();
            tickDigits = (int) normalizedInterval.getMantissa();
            tickPower = normalizedInterval.getPower();
            labelFormat = getNumberFormat(tickPower, labelFormatInfo);
        }

        @Override
        public void increaseTickInterval(int increaseFactor) {
            tickInterval *= increaseFactor;
            tickDigits *= increaseFactor;
            while (tickDigits % 10 == 0) {
                tickDigits /= 10;
                tickPower++;
            }
            labelFormat = getNumberFormat(tickPower, labelFormatInfo);
        }

        @Override
        public Tick getNextTick() {
            if (lastTick == null) {
                double min = domain[0];
                lastTick = getLowerTick(min);
            } else {
                double tickValue = lastTick.getValue() + tickInterval;
                lastTick = new Tick(tickValue, labelFormat.format(tickValue));
            }
            return lastTick;
        }

        @Override
        public Tick getPreviousTick() {
            if (lastTick == null) {
                double min = domain[0];
                lastTick = getLowerTick(min);
            } else {
                double tickValue = lastTick.getValue() - tickInterval;
                lastTick = new Tick(tickValue, labelFormat.format(tickValue));
            }
            return lastTick;
        }

        @Override
        public Tick getUpperTick(double value) {
            double tickValue = Math.ceil(value / tickInterval) * tickInterval;
            lastTick = new Tick(tickValue, labelFormat.format(tickValue));
            return lastTick;
        }

        @Override
        public Tick getLowerTick(double value) {
            double tickValue = Math.floor(value / tickInterval) * tickInterval;
            lastTick = new Tick(tickValue, labelFormat.format(tickValue));
            return lastTick;
        }


        /**
         * Calculates round Tick Interval
         * that is  multiples of 2, 5 or 10.
         * FirstDigit is in {1,2,5,10};
         */
        private NormalizedNumber getRoundTickInterval(int tickIntervalCount) {
            if (tickIntervalCount <= 1) {
                String errMsg = MessageFormat.format("Invalid tick interval count: {0}. Expected >= 2", tickIntervalCount);
                throw new IllegalArgumentException(errMsg);
            }
            double max = domain[domain.length - 1];
            double min = domain[0];
            double step = (max - min) / tickIntervalCount;
            NormalizedNumber normalizedStep = new NormalizedNumber(step);

            int power = normalizedStep.getPower();
            int firstDigit = (int) Math.round(normalizedStep.getMantissa());
            switch (firstDigit) {
                case 3:
                    firstDigit = 2;
                    break;
                case 4:
                    firstDigit = 5;
                    break;
                case 6:
                    firstDigit = 5;
                    break;
                case 7:
                    firstDigit = 5;
                    break;
                case 8:
                    firstDigit = 1;
                    power++;
                    break;
                case 9:
                    firstDigit = 1;
                    power++;
                    break;
                case 10:
                    firstDigit = 1;
                    power++;
                    break;
            }
            return new NormalizedNumber(firstDigit, power);
        }
    }
}
