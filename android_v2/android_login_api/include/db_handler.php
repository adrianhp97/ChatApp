<?php

class DbHandler {
 
    private $conn;
 
    function __construct() {
        require_once dirname(__FILE__) . '/db_connect.php';
        // opening db connection
        $db = new DbConnect();
        $this->conn = $db->connect();
    }
 
    // creating new user if not existed
    public function createUser($name, $email, $password) {
        $response = array();
 
        // First check if user already existed in db
        if (!$this->isUserExists($email)) {
            $uuid = uniqid('', true);
            $hash = $this->hashSSHA($password);
            $encrypted_password = $hash["encrypted"]; // encrypted password
            $salt = $hash["salt"]; // salt

            // insert query
            $stmt = $this->conn->prepare("INSERT INTO users(name, email, gcm_registration_id, encrypted_password, salt) values(?, ?, ?, ?, ?)");
            $stmt->bind_param("sssss", $name, $email, $email, $encrypted_password, $salt);
 
            $result = $stmt->execute();
 
            $stmt->close();
 
            // Check for successful insertion
            if ($result) {
                // User successfully inserted
                $response["error"] = false;
                $response["user"] = $this->getUserByEmail($email);
            } else {
                // Failed to create user
                $response["error"] = true;
                $response["message"] = "Oops! An error occurred while registering";
            }
        } else {
            // User with same email already existed in the db
            $response["error"] = true;
            $response["user"] = $this->getUserByEmail($email);
        }
 
        return $response;
    }

     // creating new room if not existed
    public function createRoom($name, $password) {
        $response = array();
 
        // First check if room already existed in db
        if (!$this->isRoomExists($name)) {
            // insert query
            $uuid = uniqid('', true);
            $hash = $this->hashSSHA($password);
            $encrypted_password = $hash["encrypted"]; // encrypted password
            $salt = $hash["salt"]; // salt
    
            $stmt = $this->conn->prepare("INSERT INTO chat_rooms(name, encrypted_password, salt) values(?, ?, ?)");
            $stmt->bind_param("sss", $name, $encrypted_password, $salt);
 
            $result = $stmt->execute();
 
            $stmt->close();
 
            // Check for successful insertion
            if ($result) {
                // User successfully inserted
                $response["error"] = false;
                $response["message"] = "Room created";
                $response["chat_room"] = $this->getRoomByName($name);
            } else {
                // Failed to create user
                $response["error"] = true;
                $response["message"] = "Oops! An error occurred while registering";
            }
        } else {
            // User with same email already existed in the db
            $response["error"] = true;
            $response["message"] = "Room already exist";
            $response["chat_room"] = $this->getRoomByName($name);
        }
 
        return $response;
    }

    public function deleteRoom($name) {
        $response = array();
 
        // First check if room already existed in db
        if ($this->isRoomExists($name)) {
            // delete query
            $stmt = $this->conn->prepare("INSERT INTO chat_rooms(name) values(?)");
            $stmt->bind_param("s", $name);
 
            $result = $stmt->execute();
 
            $stmt->close();
 
            // Check for successful insertion
            if ($result) {
                // User successfully inserted
                $response["error"] = t;
                $response["chat_room"] = $this->getRoomByName($name);
            } else {
                // Failed to create user
                $response["error"] = true;
                $response["message"] = "Oops! An error occurred while registering";
            }
        } else {
            $response["error"] = true;
            $response["user"] = $this->getRoomByName($name);
        }
 
        return $response;
    }

    // creating new user if not existed
    public function loginUser($email, $password) {
        $response = array();

        // First check if user already existed in db
        if ($this->isUserExists($email)) {
            // insert query
            $stmt = $this->conn->prepare("SELECT encrypted_password, salt from users WHERE email = ?");
            $stmt->bind_param("s", $email);
 
            $result = $stmt->execute();
            $user = $stmt->get_result()->fetch_assoc();
 
            $stmt->close();

            $salt = $user['salt'];
            $encrypted_password = $user['encrypted_password'];
            $hash = $this->checkhashSSHA($salt, $password);
 
            if ($encrypted_password == $hash) {
                $response["error"] = false;
                $response["user"] = $this->getUserByEmail($email);
            } else {
                // Failed to login
                $response["error"] = true;
                $response["message"] = "Oops! Login Failed";
            }
        } else {
            // Failed to login
                $response["error"] = true;
                $response["message"] = "Oops! There's no user with that email";
        }
 
        return $response;
    }

    // join room if existed
    public function joinRoom($user_id, $name, $password) {
        $response = array();

        // First check if user already existed in db
        if ($this->isRoomExists($name)) {
            // insert query
            $stmt = $this->conn->prepare("SELECT chat_room_id, encrypted_password, salt from chat_rooms WHERE name = ?");
            $stmt->bind_param("s", $name);
 
            $result = $stmt->execute();
            $room = $stmt->get_result()->fetch_assoc();
 
            $stmt->close();

            $salt = $room['salt'];
            $room_id = $room['chat_room_id'];
            $encrypted_password = $room['encrypted_password'];
            $hash = $this->checkhashSSHA($salt, $password);
 
            if ($encrypted_password == $hash) {
                $response["error"] = false;
                $response["message"] = $this->createRelationshipRoom($room_id, $user_id);
                $response["chat_room"] = $this->getRoomByName($name);
            } else {
                // Failed to login
                $response["error"] = true;
                $response["message"] = "Oops! Failed to join the group";
            }
        } else {
            // Failed to login
                $response["error"] = true;
                $response["message"] = "Oops! There's no room with that name";
        }
 
        return $response;
    }

    // creating new user if not existed
    public function inviteToRoom($email, $chat_room_id) {
        $response = array();

        // First check if user already existed in db
        if ($this->isUserExists($email)) {
            // insert query
            $stmt = $this->conn->prepare("SELECT user_id from users WHERE email = ?");
            $stmt->bind_param("s", $email);
 
            $result = $stmt->execute();
            $user = $stmt->get_result()->fetch_assoc();
 
            $stmt->close();

            $user_id = $user['user_id'];
 
            if ($result) {
                $response["error"] = false;
                $response["message"] = $this->createRelationshipRoom($chat_room_id, $user_id);
            } else {
                // Failed to login
                $response["error"] = true;
                $response["message"] = "Oops! Failed to invite user to the group";
            }
        } else {
            // Failed to login
                $response["error"] = true;
                $response["message"] = "Oops! There's no user with that email";
        }
 
        return $response;
    }

    public function createRelationshipRoom($room_id, $user_id) {
        $stmt = $this->conn->prepare("INSERT INTO chat_room_by_user(chat_room_id, user_id) values(?, ?)");
        $stmt->bind_param("ii", $room_id, $user_id);
        $result = $stmt->execute();
        $stmt->close();
        return "success";
    }
 
    // updating user GCM registration ID
    public function updateGcmID($user_id, $gcm_registration_id) {
        $response = array();
        $stmt = $this->conn->prepare("UPDATE users SET gcm_registration_id = ? WHERE user_id = ?");
        $stmt->bind_param("si", $gcm_registration_id, $user_id);
 
        if ($stmt->execute()) {
            // User successfully updated
            $response["error"] = false;
            $response["message"] = 'GCM registration ID updated successfully';
        } else {
            // Failed to update user
            $response["error"] = true;
            $response["message"] = "Failed to update GCM registration ID" . $user_id . "gds";
            $stmt->error;
        }
        $stmt->close();
 
        return $response;
    }
 
    // fetching single user by id
    public function getUser($user_id) {
        $stmt = $this->conn->prepare("SELECT user_id, name, email, gcm_registration_id, created_at FROM users WHERE user_id = ?");
        $stmt->bind_param("s", $user_id);
        if ($stmt->execute()) {
            // $user = $stmt->get_result()->fetch_assoc();
            $stmt->bind_result($user_id, $name, $email, $gcm_registration_id, $created_at);
            $stmt->fetch();
            $user = array();
            $user["user_id"] = $user_id;
            $user["name"] = $name;
            $user["email"] = $email;
            $user["gcm_registration_id"] = $gcm_registration_id;
            $user["created_at"] = $created_at;
            $stmt->close();
            return $user;
        } else {
            return NULL;
        }
    }
 
    // fetching multiple users by ids
    public function getUsers($user_ids) {
 
        $users = array();
        if (sizeof($user_ids) > 0) {
            $query = "SELECT user_id, name, email, gcm_registration_id, created_at FROM users WHERE user_id IN (";
 
            foreach ($user_ids as $user_id) {
                $query .= $user_id . ',';
            }
 
            $query = substr($query, 0, strlen($query) - 1);
            $query .= ')';
 
            $stmt = $this->conn->prepare($query);
            $stmt->execute();
            $result = $stmt->get_result();
 
            while ($user = $result->fetch_assoc()) {
                $tmp = array();
                $tmp["user_id"] = $user['user_id'];
                $tmp["name"] = $user['name'];
                $tmp["email"] = $user['email'];
                $tmp["gcm_registration_id"] = $user['gcm_registration_id'];
                $tmp["created_at"] = $user['created_at'];
                array_push($users, $tmp);
            }
        }
 
        return $users;
    }
 
    // messaging in a chat room / to persional message
    public function addMessage($user_id, $chat_room_id, $message) {
        $response = array();
 
        $stmt = $this->conn->prepare("INSERT INTO messages (chat_room_id, user_id, message) values(?, ?, ?)");
        $stmt->bind_param("iis", $chat_room_id, $user_id, $message);
 
        $result = $stmt->execute();
 
        if ($result) {
            $response['error'] = false;
 
            // get the message
            $message_id = $this->conn->insert_id;
            $stmt = $this->conn->prepare("SELECT message_id, user_id, chat_room_id, message, created_at FROM messages WHERE message_id = ?");
            $stmt->bind_param("i", $message_id);
            if ($stmt->execute()) {
                $stmt->bind_result($message_id, $user_id, $chat_room_id, $message, $created_at);
                $stmt->fetch();
                $tmp = array();
                $tmp['message_id'] = $message_id;
                $tmp['chat_room_id'] = $chat_room_id;
                $tmp['message'] = $message;
                $tmp['created_at'] = $created_at;
                $response['message'] = $tmp;
            }
        } else {
            $response['error'] = true;
            $response['message'] = 'Failed send message';
        }
 
        return $response;
    }
 
    // fetching all chat rooms
    public function getAllChatrooms() {
        $stmt = $this->conn->prepare("SELECT * FROM chat_rooms");
        $stmt->execute();
        $tasks = $stmt->get_result();
        $stmt->close();
        return $tasks;
    }

    // fetching all chat rooms
    public function getAllChatroomsById($user_id) {
        $stmt = $this->conn->prepare("SELECT crbu.chat_room_id, cr.name, cr.created_at FROM chat_rooms cr LEFT JOIN chat_room_by_user crbu ON cr.chat_room_id = crbu.chat_room_id WHERE user_id = ?");
        $stmt->bind_param("i", $user_id);        
        $stmt->execute();
        $tasks = $stmt->get_result();
        $stmt->close();
        return $tasks;
    }
 
    // fetching single chat room by id
    function getChatRoom($chat_room_id) {
        $stmt = $this->conn->prepare("SELECT cr.chat_room_id, cr.name, cr.created_at as chat_room_created_at, u.name as username, c.* FROM chat_rooms cr LEFT JOIN messages c ON c.chat_room_id = cr.chat_room_id LEFT JOIN users u ON u.user_id = c.user_id WHERE cr.chat_room_id = ?");
        $stmt->bind_param("i", $chat_room_id);
        $stmt->execute();
        $tasks = $stmt->get_result();
        $stmt->close();
        return $tasks;
    }
 
    /**
     * Checking for duplicate user by email address
     * @param String $email email to check in db
     * @return boolean
     */
    private function isUserExists($email) {
        $stmt = $this->conn->prepare("SELECT user_id from users WHERE email = ?");
        $stmt->bind_param("s", $email);
        $stmt->execute();
        $stmt->store_result();
        $num_rows = $stmt->num_rows;
        $stmt->close();
        return $num_rows > 0;
    }

    private function isRoomExists($name) {
        $stmt = $this->conn->prepare("SELECT chat_room_id from chat_rooms WHERE name = ?");
        $stmt->bind_param("s", $name);
        $stmt->execute();
        $stmt->store_result();
        $num_rows = $stmt->num_rows;
        $stmt->close();
        return $num_rows > 0;
    }
 
    /**
     * Fetching user by email
     * @param String $email User email id
     */
    public function getUserByEmail($email) {
        $stmt = $this->conn->prepare("SELECT user_id, name, email, created_at FROM users WHERE email = ?");
        $stmt->bind_param("s", $email);
        if ($stmt->execute()) {
            // $user = $stmt->get_result()->fetch_assoc();
            $stmt->bind_result($user_id, $name, $email, $created_at);
            $stmt->fetch();
            $user = array();
            $user["user_id"] = $user_id;
            $user["name"] = $name;
            $user["email"] = $email;
            $user["created_at"] = $created_at;
            $stmt->close();
            return $user;
        } else {
            return NULL;
        }
    }

    public function getRoomByName($name) {
        $stmt = $this->conn->prepare("SELECT chat_room_id, name, encrypted_password, salt, created_at FROM chat_rooms WHERE name = ?");
        $stmt->bind_param("s", $name);
        if ($stmt->execute()) {
            // $user = $stmt->get_result()->fetch_assoc();
            $stmt->bind_result($chat_room_id, $name, $encrypted_password, $salt, $created_at);
            $stmt->fetch();
            $chat_room = array();
            $chat_room["chat_room_id"] = $chat_room_id;
            $chat_room["name"] = $name;
            $chat_room["encrypted_password"] = $encrypted_password;
            $chat_room["salt"] = $salt;
            $chat_room["created_at"] = $created_at;
            $stmt->close();
            return $chat_room;
        } else {
            return NULL;
        }
    }

    // creating new event
    public function createEvent($name, $desc, $start, $end, $location) {
        $response = array();

        // insert query
        $stmt = $this->conn->prepare("INSERT INTO events(name, description, start_at, end_at, location) values(?, ?, ?, ?, ?)");
        $stmt->bind_param("sssss", $name, $desc, $start, $end, $location);

        $result = $stmt->execute();

        $stmt->close();

        // Check for successful insertion
        if ($result) {
            // User successfully inserted
            $response["error"] = false;
            $response["message"] = "Success!";
        } else {
            // Failed to create user
            $response["error"] = true;
            $response["message"] = "Oops! An error occurred while registereing";
        }

        return $response;
    }
	
	// deleting event
    public function deleteEvent($id) {
        $response = array();

		// insert query
		$stmt = $this->conn->prepare("DELETE FROM events WHERE event_id = ?");
		$stmt->bind_param("s", $id);

		$result = $stmt->execute();

		$stmt->close();

		// Check for successful insertion
		if ($result) {
			// User successfully inserted
			$response["error"] = false;
			$response["message"] = "Success!";
		} else {
			// Failed to create user
			$response["error"] = true;
			$response["message"] = "Oops! An error occurred while registereing";
		}

        return $response;
    }
	
	// fetching all events
    public function getAllEvents() {
        $stmt = $this->conn->prepare("SELECT * FROM events");
        $stmt->execute();
        $tasks = $stmt->get_result();
        $stmt->close();
        return $tasks;
    }


    /**
     * Encrypting password
     * @param password
     * returns salt and encrypted password
     */
    public function hashSSHA($password) {
 
        $salt = sha1(rand());
        $salt = substr($salt, 0, 10);
        $encrypted = base64_encode(sha1($password . $salt, true) . $salt);
        $hash = array("salt" => $salt, "encrypted" => $encrypted);
        return $hash;
    }
 
    /**
     * Decrypting password
     * @param salt, password
     * returns hash string
     */
    public function checkhashSSHA($salt, $password) {
 
        $hash = base64_encode(sha1($password . $salt, true) . $salt);
 
        return $hash;
    }
 
}
 
?>
