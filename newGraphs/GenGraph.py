import snap
import sys

#Rnd = snap.TRnd()
#UGraph = snap.GenPrefAttach(100, 2, Rnd)

n = int (sys.argv[1]) #number of nodes
l = float (sys.argv[2]) #lambda
UGraph = snap.GenRndPowerLaw(n, l)

for EI in UGraph.Edges():
    print "%d, %d" % (EI.GetSrcNId(), EI.GetDstNId())
