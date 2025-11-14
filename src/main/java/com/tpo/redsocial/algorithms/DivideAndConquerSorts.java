package com.tpo.redsocial.algorithms;

import java.util.Arrays;

public class DivideAndConquerSorts {

    public static void quickSort(double[] a) {
        if (a == null || a.length < 2) return;
        quick(a, 0, a.length - 1);
    }

    private static void quick(double[] a, int l, int r) {
        int i = l;
        int j = r;
        double pivot = a[(l + r) / 2];

        while (i <= j) {
            while (a[i] < pivot) i++;
            while (a[j] > pivot) j--;

            if (i <= j) {
                double tmp = a[i];
                a[i] = a[j];
                a[j] = tmp;
                i++;
                j--;
            }
        }

        if (l < j) quick(a, l, j);
        if (i < r) quick(a, i, r);
    }

    public static double[] mergeSort(double[] a) {
        if (a == null || a.length <= 1) return a;
        int mid = a.length / 2;

        double[] left = mergeSort(Arrays.copyOfRange(a, 0, mid));
        double[] right = mergeSort(Arrays.copyOfRange(a, mid, a.length));

        return merge(left, right);
    }

    private static double[] merge(double[] l, double[] r) {
        double[] res = new double[l.length + r.length];

        int i = 0, j = 0, k = 0;

        while (i < l.length && j < r.length) {
            res[k++] = (l[i] <= r[j]) ? l[i++] : r[j++];
        }

        while (i < l.length) res[k++] = l[i++];
        while (j < r.length) res[k++] = r[j++];

        return res;
    }
}
