package com.biorecorder.basechart.chart.scales;

import com.biorecorder.basechart.chart.config.LabelFormatInfo;

import java.text.DecimalFormat;
import java.text.MessageFormat;

/**
 * Created by galafit on 6/9/17.
 */
public class LinearScale implements Scale {
    DecimalFormat numberFormatter;
    private double domain[] = {0, 1};
    private float range[] = {0, 1};

    @Override
    public void setDomain(double... domain) {
        this.domain = domain;
        numberFormatter = null;
    }

    @Override
    public void setRange(float... range) {
        this.range = range;
        numberFormatter = null;
    }

    @Override
    public double[] getDomain() {
        return domain;
    }

    @Override
    public float[] getRange() {
        return range;
    }

    @Override
    public float scale(double value) {
        return (float) (range[0] + (value - domain[0]) * (range[range.length - 1] - range[0]) / (domain[domain.length - 1] - domain[0]));
    }

    @Override
    public double invert(float value) {
        return domain[0] + (value - range[0]) * (domain[domain.length - 1] - domain[0]) / (range[range.length - 1] - range[0]);
    }

    @Override
    public TickProvider getTickProvider(int tickCount, LabelFormatInfo labelFormatInfo) {
        return new LinearTickProvider(tickCount, labelFormatInfo);
    }

    @Override
    public TickProvider getTickProvider(double tickStep, Unit tickUnit, LabelFormatInfo labelFormatInfo) {
        return new LinearTickProvider(tickStep, tickUnit, labelFormatInfo);
    }

    @Override
    public String formatDomainValue(double value) {
        if (numberFormatter == null) {
            float rangePointsCount = (int)Math.abs(range[range.length - 1] - range[0]) + 1;
            double domainLength = domain[domain.length - 1] - domain[0];
            NormalizedNumber pointStep = new NormalizedNumber(domainLength / rangePointsCount);
            numberFormatter = getNumberFormat(pointStep.getPower(), null);
        }
        long longPart = (long) value;
        if(value == longPart) {
            return String.valueOf(longPart);
        }
        return numberFormatter.format(value);
    }

    // TODO: use metric shortcuts - k, M, G... from formatInfo
    private DecimalFormat getNumberFormat(int power, LabelFormatInfo labelFormatInfo) {
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
        private double tickStep;
        int tickDigits;
        int tickPower;
        private LabelFormatInfo labelFormatInfo;
        private DecimalFormat labelFormat = new DecimalFormat();
        private Tick lastTick;

        public LinearTickProvider(int tickCount, LabelFormatInfo labelFormatInfo) {
            this.labelFormatInfo = labelFormatInfo;
            NormalizedNumber normalizedStep = getRoundTickStep(tickCount);
            tickStep = normalizedStep.getValue();
            tickDigits = (int) normalizedStep.getMantissa();
            tickPower = normalizedStep.getPower();
            labelFormat = getNumberFormat(tickPower, labelFormatInfo);
        }

        public LinearTickProvider(double tickStep, Unit tickUnit, LabelFormatInfo labelFormatInfo) {
            this.tickStep = tickStep;
            this.labelFormatInfo = labelFormatInfo;
            NormalizedNumber normalizedStep = new NormalizedNumber(tickStep);
            tickPower = normalizedStep.getPowerOfLastSignificantDigit();
            tickDigits = (int) (normalizedStep.getMantissa() * Math.pow(10, tickPower));
            labelFormat = getNumberFormat(tickPower, labelFormatInfo);
        }

        @Override
        public void increaseTickStep(int increaseFactor) {
            tickStep *= increaseFactor;
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
                double tickValue = lastTick.getValue() + tickStep;
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
                double tickValue = lastTick.getValue() - tickStep;
                lastTick = new Tick(tickValue, labelFormat.format(tickValue));
            }
            return lastTick;
        }

        @Override
        public Tick getUpperTick(double value) {
            double tickValue = Math.ceil(value / tickStep) * tickStep;
            lastTick = new Tick(tickValue, labelFormat.format(tickValue));
            return lastTick;
        }

        @Override
        public Tick getLowerTick(double value) {
            double tickValue = Math.floor(value / tickStep) * tickStep;
            lastTick = new Tick(tickValue, labelFormat.format(tickValue));
            return lastTick;
        }


        /**
         * On the com.biorecorder.basechart.chart of ticks amount calculate round Tick Step
         * that is  multiples of 2, 5 or 10.
         * FirstDigit is in {1,2,5,10};
         */
        private NormalizedNumber getRoundTickStep(int tickCount) {
            if (tickCount <= 1) {
                String errMsg = MessageFormat.format("Invalid ticks tickCount: {0}. Expected >= 2", tickCount);
                throw new IllegalArgumentException(errMsg);
            }
            double max = domain[domain.length - 1];
            double min = domain[0];
            double step = (max - min) / (tickCount - 1);
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

        /**
         * Find closest roundStep >= given step
         *
         * @param step given step
         * @return closest roundInterval >= given step
         */
        private NormalizedNumber roundStepUp(double step) {
            // int[] roundValues = {1, 2, 3, 4, 5, 6, 8, 10};
            int[] roundSteps = {1, 2, 4, 5, 8, 10};
            NormalizedNumber normalizedStep = new NormalizedNumber(step);
            int power = normalizedStep.getPower();
            int firstDigit = (int) normalizedStep.getMantissa();
            if (firstDigit < normalizedStep.getMantissa()) {
                firstDigit++;
            }

            // find the closest roundStep that is >= firstDigits
            for (int roundStep : roundSteps) {
                if (roundStep >= firstDigit) {
                    firstDigit = roundStep;
                    break;
                }
            }
            if (firstDigit == 10) {
                firstDigit = 1;
                power++;
            }
            return new NormalizedNumber(firstDigit, power);
        }

        /**
         * Find and set round ticksStep such that:
         * resultantTicksAmount <= maxCount
         *
         * @param maxCount - desirable amount of ticks
         */
        private NormalizedNumber getStepForMaxTickCount(int maxCount) {
            if (maxCount <= 1) {
                String errMsg = MessageFormat.format("Invalid ticks amount: {0}. Expected >= 2", maxCount);
                throw new IllegalArgumentException(errMsg);
            }
            double max = domain[domain.length - 1];
            double min = domain[0];
            double step = (max - min) / (maxCount - 1);
            NormalizedNumber roundStep = roundStepUp(step);
            tickStep = roundStep.getValue();
            int ticksCount = (int) ((getLowerTick(min).getValue() - getUpperTick(max).getValue()) / tickStep) + 1;

        /*
         * Due to rounding (roundMin < getMin < max < roundMax)
         * sometimes it is possible that the resultant ticksCount may be
         * greater than the maxCount:
         * resultantAmount = maxCount + 1.
         * In this case we repeat the same procedure with (maxCount -1)
         */
            if (ticksCount > maxCount && maxCount > 2) {
                maxCount--;
                step = (max - min) / (maxCount - 1);
                roundStep = roundStepUp(step);
            }
            return roundStep;
        }
    }
}
