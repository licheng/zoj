package cn.edu.zju.acm.onlinejudge.judgeserver;

import cn.edu.zju.acm.onlinejudge.bean.Submission;

class SubmissionCache {
    private static class InternalArrayList {
	Submission[] data;

	int size = 0;

	public InternalArrayList(int capacity) {
	    data = new Submission[capacity];
	}
    }

    private SubmissionCache.InternalArrayList data1, data2;

    private int arrayCapacity;

    private Submission[] emptySubmissionArray = new Submission[0];

    public SubmissionCache(int capacity) {
	for (int i = 2;; i *= 2) {
	    if (i >= capacity) {
		capacity = i;
		break;
	    }
	}
	arrayCapacity = capacity / 2;
	data1 = new InternalArrayList(this.arrayCapacity);
    }

    public synchronized void enlarge() {
	arrayCapacity *= 2;
	InternalArrayList temp = new InternalArrayList(arrayCapacity);
	if (data2 == null) {
	    temp.size = data1.size;
	    System.arraycopy(data1, 0, temp, 0, data1.size);
	} else {
	    temp.size = data1.size + data2.size;
	    System.arraycopy(data2, 0, temp, 0, data2.size);
	    System.arraycopy(data1, 0, temp, data1.size, data2.size);
	}
    }
    
    public int getCapacity() {
	return arrayCapacity * 2;
    }

    public synchronized void add(Submission submission) {
	if (data1.size == this.arrayCapacity) {
	    data2 = data1;
	    data1 = new InternalArrayList(arrayCapacity);
	}
	data1.data[data1.size++] = submission;
    }

    public Submission[] getSubmissions() {
	return getSubmissions(0, this.arrayCapacity * 2);
    }

    public Submission[] getSubmissions(int start, int end) {
	SubmissionCache.InternalArrayList d1 = data1;
	SubmissionCache.InternalArrayList d2 = data2;
	int currentD1Size = d1.size;
	Submission[] submissions;
	if (end > arrayCapacity + currentD1Size) {
	    end = arrayCapacity + currentD1Size;
	}
	if (d2 == null && end > currentD1Size) {
	    end = currentD1Size;
	}
	if (end <= currentD1Size) {
	    submissions = new Submission[end - start];
	    System.arraycopy(d1.data, currentD1Size - end, submissions, 0, end - start);
	} else if (start >= currentD1Size) {
	    if (d2 == null) {
		return emptySubmissionArray;
	    }
	    submissions = new Submission[end - start];
	    System.arraycopy(d2.data, arrayCapacity + currentD1Size - end, submissions, 0, end - start);
	} else {
	    submissions = new Submission[end - start];
	    System.arraycopy(d1.data, 0, submissions, end - currentD1Size, currentD1Size - start);
	    System.arraycopy(d2.data, arrayCapacity + currentD1Size - end, submissions, 0, end - currentD1Size);
	}
	return submissions;
    }
}