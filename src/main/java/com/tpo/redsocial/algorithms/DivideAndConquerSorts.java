// src/main/java/com/tpo/redsocial/algorithms/DivideAndConquerSorts.java
package com.tpo.redsocial.algorithms;
import java.util.*;

public class DivideAndConquerSorts {
    public static void quickSort(double[] a, int l, int r){
        if (l>=r) return;
        double p=a[(l+r)/2];
        int i=l,j=r;
        while(i<=j){
            while(a[i]<p) i++;
            while(a[j]>p) j--;
            if (i<=j){ double t=a[i]; a[i]=a[j]; a[j]=t; i++; j--; }
        }
        if (l<j) quickSort(a,l,j);
        if (i<r) quickSort(a,i,r);
    }
    public static double[] mergeSort(double[] a){
        if (a.length<=1) return a;
        int m=a.length/2;
        return merge(mergeSort(Arrays.copyOfRange(a,0,m)),
                     mergeSort(Arrays.copyOfRange(a,m,a.length)));
    }
    private static double[] merge(double[] L,double[] R){
        double[] res=new double[L.length+R.length]; int i=0,j=0,k=0;
        while(i<L.length && j<R.length) res[k++]= (L[i]<=R[j])? L[i++]:R[j++];
        while(i<L.length) res[k++]=L[i++]; while(j<R.length) res[k++]=R[j++];
        return res;
    }
}
