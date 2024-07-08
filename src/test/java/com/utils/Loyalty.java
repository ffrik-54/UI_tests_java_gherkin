package com.utils;

import java.security.SecureRandom;
import java.text.DecimalFormat;

public class Loyalty {

    private static final int[] validUsAreaCodes= {
            205, 251, 256, 334, 659, 938, 907, 480, 520, 602, 623, 928, 327, 479, 501, 870,
            209, 213, 279, 310, 323, 341, 350, 369, 408, 415, 424, 442, 510, 530, 559, 562,
            619, 626, 628, 650, 657, 661, 669, 707, 714, 747, 760, 805, 818, 820, 831, 840,
            858, 909, 916, 925, 949, 951, 303, 719, 720, 970, 983, 203, 475, 860, 959, 302,
            202, 771, 239, 305, 321, 324, 352, 386, 407, 448, 561, 645, 656, 689, 727, 728,
            754, 772, 786, 813, 850, 863, 904, 941, 954, 229, 404, 470, 478, 678, 706, 762,
            770, 912, 943, 808, 208, 986, 217, 224, 309, 312, 331, 447, 464, 618, 630, 708,
            730, 773, 779, 815, 847, 861, 872, 219, 260, 317, 463, 574, 765, 812, 930, 319,
            515, 563, 641, 712, 316, 620, 785, 913, 270, 364, 502, 606, 859, 225, 318, 337,
            457, 504, 985, 207, 227, 240, 301, 410, 443, 667, 339, 351, 413, 508, 617, 774,
            781, 857, 978, 231, 248, 269, 313, 517, 586, 616, 679, 734, 810, 906, 947, 989,
            218, 320, 507, 612, 651, 763, 924, 952, 228, 601, 662, 769, 235, 314, 417, 557,
            573, 636, 660, 816, 975, 406, 308, 402, 531, 702, 725, 775, 603, 201, 551, 609,
            640, 732, 848, 856, 862, 908, 973, 505, 575, 212, 315, 329, 332, 347, 363, 516,
            518, 585, 607, 624, 631, 646, 680, 716, 718, 838, 845, 914, 917, 929, 934, 252,
            336, 472, 704, 743, 828, 910, 919, 980, 984, 701, 216, 220, 234, 283, 326, 330,
            380, 419, 436, 440, 513, 567, 614, 740, 937, 405, 539, 572, 580, 918, 458, 503,
            541, 971, 215, 223, 267, 272, 412, 445, 484, 570, 582, 610, 717, 724, 814, 835,
            878, 401, 803, 821, 839, 843, 854, 864, 605, 423, 615, 629, 731, 865, 901, 931,
            210, 214, 254, 281, 325, 346, 361, 409, 430, 432, 469, 512, 682, 713, 726, 737,
            806, 817, 830, 832, 903, 915, 936, 940, 945, 956, 972, 979, 385, 435, 801, 802,
            276, 434, 540, 571, 686, 703, 757, 804, 826, 948, 206, 253, 360, 425, 509, 564,
            304, 681, 262, 274, 353, 414, 534, 608, 715, 920, 307
    };

    private static final SecureRandom rand = new SecureRandom();

    /**
        Generates a random US phone number to be used in Loyalty program sign up
        The first 3 digits are greater than 100 but not contain 8 or 9.
        The second set of three digits is not greater than 742
    **/
    public static String generateUsPhoneNumber() {
        int num1 = validUsAreaCodes[rand.nextInt(validUsAreaCodes.length)];

        int num2 = 200 + rand.nextInt(543);
        int num3 = rand.nextInt(10000);

        DecimalFormat df3 = new DecimalFormat("000"); // 3 zeros
        DecimalFormat df4 = new DecimalFormat("0000"); // 4 zeros

        return df3.format(num1) + df3.format(num2).replaceFirst("^1","2") + df4.format(num3);
    }

    public static String formatAsDisplayedPhoneNumber(String number) {
        return String.format("(%s) %s-%s", number.substring(0, 3), number.substring(3, 6),
                number.substring(6, 10));
    }
}
