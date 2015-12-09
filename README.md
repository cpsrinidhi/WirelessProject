# WirelessProject #
---
### Wireless P2P File Sharing using socket programming in Java ###

This as an academic project.

Implement a wireless Peer-to-Peer network for file sharing where a central device acts as a tracker server. The tracker server keeps track of the connected peers in the network and the files available with each peer for sharing. An interested peer fetches the list of files and the respective nodes from the tracker and uses this information to directly download the file from the peer.

#### Requirements: ####
1. The tracker server must maintain the list of peers in real-time. The list of peers must be updated whenever a peer joins or leaves the network. The same should be updated on the peer devices.
2. A peer may leave the network abruptly while another peer is downloading from it. The application should either pause or cancel the download gracefully.
3. A peer should be also be able to notify the tracker if it has any files available for sharing which should then be reflected to every connected peer in the network.
4. The file list and the peer list should be visible on the peer device. The user should select the file from the list after which the download can begin. Appropriate download progress must be shown in the UI.
5. Programming framework: Android/iOS

#### Solution ####
1. Create 2 separate applications - Tracker and Peer
2. Tracker serves as a server that contains the updated list of all "shareable" files from all Peers
3. Each Peer has a file transfer running running as a background service
4. Each Peer connects to Tracker and shares its "shareable" file list. In turn, it obtains the latest file list from the Tracker.
5. Peer can then request any file directly from another Peer.

#### References ####
We are novices in android programming. We would have a tough time if not for the following coders/authors.

https://www.google.com :joy:

http://android-er.blogspot.com/2015/01/file-transfer-via-socket-between.html :heart_eyes:

https://stackoverflow.com/ on the whole. Cannot specify a single link. :stuck_out_tongue_closed_eyes:
