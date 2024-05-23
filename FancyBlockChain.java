public class FancyBlockChain {
    public Block[] bchain;
    public int numBlocks;
    public int maxTimeStamp;

    public FancyBlockChain(int capacity) {
        // making new empty bchain array that is the length of capacity
        bchain = new Block[capacity];
        numBlocks = 0;
        maxTimeStamp = 0;
    }

    public FancyBlockChain(Block[] initialBlocks) {

        // making new bchain object that is the numBlocks of initialBlocks
        bchain = new Block[initialBlocks.length];
        numBlocks = 0;
//        numBlocks = initialBlocks.length;
        maxTimeStamp = 0;

        for (int i = 0; i < initialBlocks.length; i++) {
            addBlock(initialBlocks[i]);
        }
    }

    public void sinkDown(Block[] futureHeap, int heapLength, int i) {
        // assigning smallest to the i of the smallest block of the heap and calculating left child and right child
        int smallest = i;
        int leftChild = (2 * i) + 1;
        int rightChild = (2 * i + 2);

        // if leftChild's timestamp is less than the smallest's timestamp, set smallest to leftChild
        if (leftChild < heapLength && futureHeap[leftChild].timestamp < futureHeap[smallest].timestamp) {
            smallest = leftChild;
        }

        // if rightChild's timestamp is less than the smallest's timestamp, set smallest to rightChild
        if (rightChild < heapLength && futureHeap[rightChild].timestamp < futureHeap[smallest].timestamp) {
            smallest = rightChild;
        }

        // if the smallest isn't equal to i, swap the new smallest and ith block and
        // call sinkDown on subtree
        if (smallest != i) {

            int tempIndex = futureHeap[i].index;
            int smallestIndex = futureHeap[smallest].index;

            Block temp = futureHeap[i];

            futureHeap[i] = futureHeap[smallest];
            futureHeap[i].index = tempIndex;

            futureHeap[smallest] = temp;
            futureHeap[smallest].index = smallestIndex;

            sinkDown(futureHeap, heapLength, smallest);
        }
    }

    public void swimUp(Block[] futureHeap, int i) {
        // calculate parent
        int parent = (i - 1) / 2;

        // compare current block timestamp to parent, if smaller, swap them and call function again to compare next block and parent
        if (futureHeap[i].timestamp < futureHeap[parent].timestamp) {

            int tempIndex = futureHeap[i].index;
            int parentIndex = futureHeap[parent].index;

            Block temp = futureHeap[i];

            futureHeap[i] = futureHeap[parent];
            futureHeap[i].index = tempIndex;

            futureHeap[parent] = temp;
            futureHeap[parent].index = parentIndex;

            // if the root and 2nd or 3rd block were not just swapped, keep checking up the heap
            if (parent != 0) {
                swimUp(futureHeap, parent);
            }
        }
    }

    public int length() {
        return numBlocks;
    }

    // The timestamp refers to the creation date. The earlier timestamp is smaller than the later timestamp.
    // A timestamp of 0 is the earliest possible.

    public boolean addBlock(Block newBlock) {
        if (numBlocks < bchain.length) {
            // adding newBlock to the bottom of the heap (end of array)
            numBlocks++;
            newBlock.index = numBlocks - 1;
            bchain[numBlocks - 1] = newBlock;

            if (numBlocks > 1) {
                // need to have the newest block swim up to correct position
                swimUp(bchain,numBlocks - 1);
            }

            // if the newBlock's timestamp is greater than the current maxTimeStamp, set maxTimeStamp to newBlock.timestamp
            if (newBlock.timestamp > maxTimeStamp) {
                maxTimeStamp = newBlock.timestamp;
            }
            return true;

            // if bchain is full, replace the earliest block with the new block if its timestamp is later
        } else if (numBlocks == bchain.length && newBlock.timestamp > bchain[0].timestamp) {
            // replacing the earliest block with the new block
            newBlock.index = 0;
            bchain[0].removed = true;
            bchain[0] = newBlock;

            // heapifying from the root
            sinkDown(bchain, numBlocks, 0);

            // if the newBlock's timestamp is greater than the current maxTimeStamp, set maxTimeStamp to newBlock.timestamp
            if (newBlock.timestamp > maxTimeStamp) {
                maxTimeStamp = newBlock.timestamp;
            }
            return true;
        }
        return false;
    }

    public Block getEarliestBlock() {
        return bchain[0];
    }

    public Block getBlock(String data) {
        // searching for data in heap that matches input and returning the block that has it
        for (int i = 0; i < numBlocks; i++) {
            if (bchain[i].data.equals(data)) {
                return bchain[i];
            }
        }
        return null;
    }


    public Block removeEarliestBlock() {
        // if numBlocks is 0, return null
        Block earliest;
        if (numBlocks == 0) {
            return null;
            // if numBlocks is 1, return the only block and set numBlocks to 0 and bchain[0] to null
        } else if (numBlocks == 1) {
            earliest = bchain[0];
            bchain[0].removed = true;
            bchain[0] = null;
            numBlocks = 0;

        } else {
            // if numBlocks is not 0 or 1, set the root to be the last element, delete the last element and heapify
            earliest = bchain[0];
            bchain[0].removed = true;
            bchain[0] = bchain[numBlocks - 1];
            bchain[0].index = 0;
            bchain[numBlocks - 1] = null;
            numBlocks--;

            sinkDown(bchain, numBlocks, 0);
        }
        return earliest;
    }

    public Block removeBlock(String data) {
        Block removed = null;
        for (int i = 0; i < numBlocks; i++) {
            // finding element to be deleted
            if (bchain[i].data.equals(data)) {
                removed = bchain[i];
                bchain[i].removed = true;
                bchain[i] = bchain[numBlocks - 1];
                bchain[i].index = removed.index;
                bchain[numBlocks - 1] = null;
                numBlocks--;
                // heapify the array from ith block to the earliest block after deletion of ith block
                for (int j = i; j >= 0; j--) {
                    sinkDown(bchain, numBlocks, j);
                }
                break;
            }
        }

        return removed;
    }

    public void updateEarliestBlock(double nonce) {
        // update the nonce value and timestamp of the root
        if (bchain[0] != null) {
            bchain[0].nonce = nonce;
            bchain[0].timestamp = 1 + maxTimeStamp;
            maxTimeStamp = maxTimeStamp + 1;
            // heapify after the timestamp is updated
            sinkDown(bchain, numBlocks, 0);
        }
    }

    public void updateBlock(String data, double nonce) {
        for (int i = 0; i < numBlocks; i++) {
            if (bchain[i].data.equals(data)) {
                // setting nonce and timestamp to new values
                bchain[i].nonce = nonce;
                bchain[i].timestamp = 1 + maxTimeStamp;
                maxTimeStamp = maxTimeStamp + 1;

                // heapifying after timestamp is updated
                sinkDown(bchain, numBlocks, i);
                break;
            }
        }
    }
}