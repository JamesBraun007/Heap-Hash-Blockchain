public class BlockChainSecure {
    public FancyBlockChain fbc;
    public Block[] btable;

    public BlockChainSecure(int capacity) {
        // using isPrimeSixK to determine the next prime number after capacity
        int initialCapacity = capacity;
        capacity = capacity + 1;
        while (isPrimeSixK(capacity)) {
            capacity++;
        }
        // creating empty btable and fbc(fbc.bchain) objects
        btable = new Block[capacity];
        fbc = new FancyBlockChain(initialCapacity);
    }

    public boolean isPrimeSixK(int n) {
        if (n <= 1) {
            return true;
        }
        if (n <= 3) {
            return false;
        }
        if (n % 2 == 0 || n % 3 == 0) {
            return true;
        }
        for (int i = 5; i < n - 2; i += 6) {
            if (n % i == 0 || n % (i + 2) == 0) {
                return true;
            }
        }
        return false;
    }

    public BlockChainSecure(Block[] initialBlocks) {
        // setting up the size of the hash table
        int hashCapacity = initialBlocks.length + 1;
        while (isPrimeSixK(hashCapacity)) {
            hashCapacity++;
        }

        // creating fbc object and btable object of size hashCapacity
        btable = new Block[hashCapacity];
        fbc = new FancyBlockChain(initialBlocks);

        // for loop to insert each block in initialBlocks into the hash
        for (int i = 0; i < initialBlocks.length; i++) {
            insertInHash(initialBlocks[i]);
        }
    }

    public int doubleHashing(String data) {
        int k = 0;
        int hashKey = (Hasher.hash1(data, btable.length)) % btable.length;
        while (btable[hashKey]!= null) {
            if (btable[hashKey].removed) {
                return hashKey;
            }
            k++;
            if (k >= btable.length) {
                hashKey = linearProbing(data);
            } else {
                hashKey = (Hasher.hash1(data, btable.length) + k * Hasher.hash2(data, btable.length)) % btable.length;
            }
        }
        return hashKey;
    }

    public int linearProbing(String data) {
        int k = 0;
        int hashKey = (Hasher.hash1(data, btable.length) + k) % btable.length;
        while (btable[hashKey] != null) {
            if (btable[hashKey].removed) {
                return hashKey;
            }
            k++;
            if (k >= btable.length) {
                return -1;
            }
            hashKey = (Hasher.hash1(data, btable.length) + k) % btable.length;
        }
        return hashKey;
    }


    public void insertInHash(Block block) {
        int hashKey = doubleHashing(block.data);
        btable[hashKey] = block;
    }

    public int length() {
        return fbc.numBlocks;
    }


    public boolean addBlock(Block newBlock) {
        // add block to the heap
        boolean addBlockHeap = fbc.addBlock(newBlock);

        // check if the block was added to the heap, and if it was, insert into the hash
        if (addBlockHeap) {
            insertInHash(newBlock);
        }

        // return whether the block was inserted or not
        return addBlockHeap;
    }

    public Block getEarliestBlock() {
        return fbc.bchain[0];
    }


    public Block getBlock(String data) {
        int hashKey = doubleHashingForSearching(data);
        if (hashKey == -1) {
            return null;
        } else {
            return btable[hashKey];
        }
    }

    public Block removeEarliestBlock() {
        return fbc.removeEarliestBlock();
    }

    public Block removeBlock(String data) {
        int hashKey = doubleHashingForSearching(data);
        if (hashKey != -1) {
            Block removed = btable[hashKey];
            fbc.bchain[removed.index].removed = true;
            fbc.bchain[removed.index] = fbc.bchain[fbc.numBlocks - 1];
            fbc.bchain[removed.index].index = removed.index;
            fbc.bchain[fbc.numBlocks - 1] = null;
            fbc.numBlocks--;
            // heapify the array from ith block to the earliest block after deletion of ith block
            for (int j = removed.index; j >= 0; j--) {
                fbc.sinkDown(fbc.bchain, fbc.numBlocks, j);
            }

            return removed;
        }

        return null;
    }

    public int doubleHashingForSearching(String data) {
        int k = 0;
        int hashKey = (Hasher.hash1(data, btable.length)) % btable.length;
        while (btable[hashKey]!= null) {
            if (btable[hashKey].data.equals(data) && !btable[hashKey].removed) {
                return hashKey;
            }
            k++;
            if (k >= btable.length) {
                hashKey = linearProbingForSearching(data);
                if (hashKey == -1) {
                    return -1;
                }
            } else {
                hashKey = (Hasher.hash1(data, btable.length) + k * Hasher.hash2(data, btable.length)) % btable.length;
            }
        }
        if (btable[hashKey] == null || (k < btable.length && !btable[hashKey].data.equals(data))) {
            return -1;
        }

        return hashKey;
    }

    public int linearProbingForSearching(String data) {
        int k = 0;
        int hashKey = (Hasher.hash1(data, btable.length) + k) % btable.length;
        while (btable[hashKey] != null) {
            if (btable[hashKey].data.equals(data) && !btable[hashKey].removed) {
                return hashKey;
            }
            k++;
            if (k >= btable.length) {
                return -1;
            }
            hashKey = (Hasher.hash1(data, btable.length) + k) % btable.length;
        }
        return -1;
    }

    public void updateEarliestBlock(double nonce) {
        fbc.updateEarliestBlock(nonce);
    }

    public void updateBlock(String data, double nonce) {
        int hashKey = doubleHashingForSearching(data);

        if (hashKey != -1) {
             btable[hashKey].nonce = nonce;
             btable[hashKey].timestamp = 1 + fbc.maxTimeStamp;
             fbc.maxTimeStamp = fbc.maxTimeStamp + 1;

            fbc.sinkDown(fbc.bchain, fbc.numBlocks, btable[hashKey].index);
        }
    }
}