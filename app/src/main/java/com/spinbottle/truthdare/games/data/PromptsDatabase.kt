package com.spinbottle.truthdare.games.data

import java.util.UUID

/**
 * Category of prompt/game mode
 */
enum class PromptCategory(val displayName: String) {
    FRIENDS("Friends"),
    COUPLES("Couples"),
    KIDS("Kids"),
    FAMILY("Family"),
    STUDENTS("Students"),
    PARTY("Party")
}

/**
 * Type of prompt
 */
enum class PromptItemType {
    TRUTH,
    DARE
}

/**
 * A single prompt item in the database
 */
data class PromptItem(
    val id: String = UUID.randomUUID().toString(),
    val type: PromptItemType,
    val category: PromptCategory,
    val difficulty: Difficulty,
    val text: String,
    val isSafeForKids: Boolean = true,
    var playCount: Int = 0
)

/**
 * Database of all prompts
 */
object PromptsDatabase {
    
    private val allPrompts = mutableListOf<PromptItem>()
    
    init {
        // Initialize with all category truths
        addFriendsTruths()
        addCouplesTruths()
        addKidsTruths()
        addFamilyTruths()
        addStudentsTruths()
        // Add dares
        addPartyDares()
        addCouplesDares()
        addKidsFamilyDares()
    }
    
    private fun addKidsFamilyDares() {
        // ========== KIDS SAFE DARES (80+) ==========
        val kidsDares = listOf(
            "Do your best animal impression for 30 seconds",
            "Sing the alphabet backwards",
            "Make the silliest face you can for 10 seconds",
            "Do 5 jumping jacks while making animal sounds",
            "Speak in a funny voice for 1 minute",
            "Do your best superhero pose",
            "Walk like a robot across the room",
            "Hop on one foot for 20 seconds",
            "Make up a silly dance move and teach everyone",
            "Tell a joke (even if it's bad!)",
            "Pretend to be a chicken for 30 seconds",
            "Spin around 3 times and try to walk straight",
            "Talk like a pirate for 1 minute",
            "Do your best monster impression",
            "Sing 'Twinkle Twinkle Little Star' in a funny voice",
            "Act like a dinosaur for 30 seconds",
            "Pat your head and rub your tummy at the same time",
            "Do the chicken dance",
            "Pretend you're walking through sticky mud",
            "Make up a funny handshake with someone",
            "Do your best impression of a monkey",
            "Speak only in whispers for 2 rounds",
            "Do 10 silly walks across the room",
            "Pretend to be a cat for 30 seconds",
            "Make a funny sound and have everyone copy it",
            "Walk like you're on the moon",
            "Hug a pillow and pretend it's your pet",
            "Tell everyone about your favorite cartoon",
            "Do your best impression of a fish",
            "Clap your hands and stomp your feet at the same time",
            "Pretend to swim across the room",
            "Make up a silly song about pizza",
            "Walk backwards for 20 seconds",
            "Pretend you're a balloon deflating",
            "Do your best elephant impression",
            "Skip around the room while singing",
            "Pretend to be frozen for 15 seconds",
            "Make the funniest noise you can",
            "Touch your nose with your tongue (or try!)",
            "Do 5 frog jumps across the room",
            "Pretend to be a snake for 30 seconds",
            "Yawn as dramatically as possible",
            "Act like you just won an award",
            "Pretend you're a statue",
            "Do your best dog impression",
            "Talk really really fast for 30 seconds",
            "Walk on your tippy toes everywhere",
            "Make up a superhero name for yourself",
            "Pretend to fly like an airplane",
            "Do a silly sneeze",
            "Crawl like a baby for 20 seconds",
            "Pretend you're a tree in the wind",
            "Do your best penguin waddle",
            "Talk in slow motion",
            "Pretend to be a pop star",
            "Do bunny hops across the room",
            "Make the best surprised face",
            "Pretend to eat invisible spaghetti",
            "Roll on the floor laughing (even if nothing's funny)",
            "Do your best owl impression",
            "Walk like you have giant feet",
            "Pretend to ride an invisible horse",
            "Give yourself a silly nickname",
            "Act like you're in a slow-motion movie",
            "Pretend you're a frog on a log",
            "Walk like there's sticky gum on your shoes",
            "Do your best rocket ship blast off",
            "Pretend you're an old person",
            "Wave like a princess/prince",
            "Do your favorite cartoon character voice",
            "Pretend to juggle invisible balls",
            "Make a drum solo on your legs",
            "Be a sports announcer for the next round",
            "Pretend you're shrinking really tiny",
            "Walk like a crab sideways",
            "Do your best beaver impression",
            "Give everyone high-fives",
            "Pretend to blow bubbles",
            "Walk like you're on a tightrope",
            "Freeze dance when someone claps"
        )
        
        // ========== FAMILY FUN DARES (70+) ==========
        val familyDares = listOf(
            "Share a happy memory about the person on your left",
            "Teach everyone a dance move",
            "Tell everyone something you're grateful for",
            "Give a compliment to everyone playing",
            "Share your favorite family tradition",
            "Tell a clean joke or pun",
            "Lead the group in a simple stretch",
            "Share what you love about being in this family",
            "Do a gentle impression of someone in the room",
            "Teach everyone a new word",
            "Share your favorite family vacation memory",
            "Lead the group in singing a song everyone knows",
            "Tell everyone about your first pet or dream pet",
            "Share a fun fact nobody knows about you",
            "Give the player to your right a genuine compliment",
            "Share what makes you happy",
            "Teach everyone a tongue twister",
            "Tell a story using only sound effects",
            "Share your favorite thing about each season",
            "Do a dramatic reading of a nursery rhyme",
            "Lead a 30-second dance party",
            "Share your favorite breakfast food",
            "Teach everyone a simple magic trick",
            "Share what you want to do with the family next",
            "Tell everyone your favorite book or movie",
            "Lead the group in doing 5 deep breaths",
            "Share your dream superpower",
            "Demonstrate your hidden talent",
            "Share your favorite animal and why",
            "Lead everyone in a silly group photo pose",
            "Tell everyone what you love about weekends",
            "Share a memory of learning something new",
            "Lead everyone in a round of applause",
            "Tell everyone your favorite game to play",
            "Share what you want to learn next",
            "Do a silly interview of the person next to you",
            "Share your favorite place to visit",
            "Lead a simple clapping rhythm for everyone to follow",
            "Tell everyone about your dream adventure",
            "Share your favorite holiday memory",
            "Lead the group in a cheerful chant",
            "Tell everyone about a time you helped someone",
            "Share what makes you unique",
            "Lead everyone in a group thumbs up",
            "Tell everyone your favorite dessert",
            "Share a dream you have for the future",
            "Lead everyone in a wave (like at sports)",
            "Tell everyone about your favorite music",
            "Share something kind someone did for you",
            "Lead the group in a funny face contest",
            "Tell everyone about your favorite outdoor activity",
            "Share what you love about your home",
            "Lead everyone in a 'hip hip hooray'",
            "Tell everyone your favorite season",
            "Share a goal you have",
            "Lead everyone in a group high-five",
            "Tell everyone about a skill you're proud of",
            "Share your favorite way to relax",
            "Lead everyone in a silly dance move",
            "Tell everyone about something you made",
            "Share what you love about today",
            "Lead everyone in saying something positive",
            "Tell everyone about your favorite event",
            "Share your happiest moment this week",
            "Lead the group in a victory pose",
            "Tell everyone about a friend you appreciate",
            "Share what makes family special",
            "Lead everyone in a gentle group hug",
            "Tell everyone something that made you smile",
            "Share your favorite way to show kindness"
        )
        
        // Add Kids dares
        kidsDares.forEach { text ->
            allPrompts.add(PromptItem(
                type = PromptItemType.DARE,
                category = PromptCategory.KIDS,
                difficulty = Difficulty.EASY,
                text = text,
                isSafeForKids = true
            ))
        }
        
        // Add Family dares
        familyDares.forEach { text ->
            allPrompts.add(PromptItem(
                type = PromptItemType.DARE,
                category = PromptCategory.FAMILY,
                difficulty = Difficulty.EASY,
                text = text,
                isSafeForKids = true
            ))
        }
    }
    
    private fun addCouplesDares() {
        // ========== SWEET COUPLES DARES (50+) - PG-13 ==========
        val sweetDares = listOf(
            "Slow dance together for 1 minute",
            "Write 3 things you love about your partner",
            "Give your partner a genuine compliment",
            "Hold hands for the next 3 rounds",
            "Tell your partner your favorite memory together",
            "Look into each other's eyes for 30 seconds",
            "Give your partner a forehead kiss",
            "Share what made you fall in love",
            "Recreate your first date pose",
            "Feed each other a snack",
            "Do a cheesy couple pose for a photo",
            "Tell your partner 3 things you appreciate about them",
            "Give your partner a warm hug for 20 seconds",
            "Describe your partner using only positive adjectives",
            "Tell your partner why they make you happy",
            "Sing a romantic song to your partner",
            "Create a nickname for your partner",
            "Plan your dream date out loud",
            "Tell your partner what you admire most about them",
            "Describe your perfect day together",
            "Give your partner a hand massage for 30 seconds",
            "Tell your partner about your favorite quality of theirs",
            "Share your most romantic dream about you two",
            "Dance together without music",
            "Write a short love note for your partner",
            "Tell your partner something you've never told them",
            "Describe your ideal future together",
            "Share what attracted you first about them",
            "Give your partner butterfly kisses",
            "Tell your partner what they mean to you",
            "Create a handshake just for you two",
            "Tell your partner your favorite thing about today",
            "Share what song reminds you of them",
            "Give your partner an eskimo kiss",
            "Tell your partner about a time they made you proud",
            "Share your favorite inside joke",
            "Describe your partner to an imaginary stranger",
            "Tell your partner 3 reasons you trust them",
            "Create a couple's bucket list item together",
            "Give your partner a back rub for 30 seconds",
            "Share your favorite photo of you two",
            "Tell your partner what you want to do together next",
            "Describe your perfect cozy night in",
            "Give your partner a shoulder massage",
            "Tell your partner how they've changed your life",
            "Share your favorite thing about mornings with them",
            "Create a couple's goal right now",
            "Tell your partner about their cutest habit",
            "Share what movie reminds you of your relationship",
            "Give your partner a sweet peck on the cheek"
        )
        
        // ========== SPICY COUPLES DARES (50+) - Adults Only ==========
        val spicyDares = listOf(
            "Give your partner a 30-second neck massage",
            "Whisper something flirty in your partner's ear",
            "Kiss your partner for 10 seconds",
            "Give your partner a shoulder rub for 1 minute",
            "Trace your finger along your partner's arm",
            "Tell your partner their most attractive feature",
            "Kiss your partner's hand romantically",
            "Sit on your partner's lap for the next round",
            "Give your partner a back massage for 1 minute",
            "Whisper a secret fantasy to your partner",
            "Play with your partner's hair for 30 seconds",
            "Kiss your partner's forehead, cheeks, then lips",
            "Give your partner a foot massage",
            "Hold your partner close for 30 seconds",
            "Tell your partner what drives you crazy about them",
            "Give your partner a sensual hand massage",
            "Kiss your partner's neck gently",
            "Dance intimately together for 1 minute",
            "Share your most romantic fantasy together",
            "Let your partner style your hair",
            "With your partner's consent, hold hands and give them a sincere compliment",
            "Give your partner a hug from behind",
            "Tell your partner something you find irresistible",
            "Slow dance with your partner to no music",
            "Give your partner a scalp massage",
            "Look into your partner's eyes and don't look away",
            "Kiss every finger on your partner's hand",
            "Tell your partner what outfit you find hottest",
            "Give your partner a gentle face massage",
            "Cuddle with your partner for the next round",
            "Whisper your favorite memory intimately",
            "With your partner's clear consent, let them guide your hands to a comfortable, non-private area",
            "Tell your partner your deepest desire",
            "Give your partner a calf massage",
            "Kiss your partner's wrist",
            "Tell your partner what they do that excites you",
            "Embrace your partner tightly for 20 seconds",
            "Ask your partner if they want a kiss, and only kiss them if they say yes",
            "Tell your partner something romantic in their ear",
            "Let your partner sit behind you and hold you",
            "Give your partner a temple massage",
            "Tell your partner what you love about their touch",
            "Kiss your partner's shoulder",
            "Nuzzle your partner's neck",
            "Give your partner a bear hug",
            "Tell your partner what moment made you want them",
            "Dance with hands on each other's waists",
            "Give your partner a warm embrace from behind",
            "Kiss your partner's ear lobe gently",
            "Tell your partner your favorite flirty memory"
        )
        
        // ========== PARTY WILD DARES (50+) - Adults Only ==========
        val wildDares = listOf(
            "Choose a photo you are comfortable showing and tell the story behind it",
            "Invent a ridiculous fake search query and explain why it would be funny",
            "Read a harmless sentence from any screen you choose",
            "Let the group invent a fictional message, but do not send it",
            "Do your best sexy walk across the room",
            "Let someone give you a ridiculous temporary tattoo",
            "Dance like no one's watching for 2 minutes",
            "Reveal your most embarrassing crush ever",
            "Do your best twerk impression for 30 seconds",
            "Let someone do your makeup blindfolded",
            "Pretend to record a dramatic social-media intro without posting it",
            "Deliver your cheesiest pickup line to the group",
            "Give someone in the room a sincere compliment",
            "Perform a 20-second victory dance",
            "Demonstrate your best pickup line on someone",
            "Tell everyone your most embarrassing first date story",
            "Do the worm across the room",
            "Let someone draw on your face with marker",
            "Tell the group your funniest autocorrect story without showing private messages",
            "Dance with a mop or broom partner",
            "Describe a funny photo without opening your gallery",
            "Do your best strip-tease dance (rated PG)",
            "Make up a fake status update and read it aloud without posting it",
            "Reveal the last person you stalked online",
            "Do a body roll against a wall",
            "Describe an embarrassing photo memory without showing your device",
            "Let someone style you however they want",
            "Do karaoke to a romantic song solo",
            "Act out how you would answer an imaginary surprise phone call",
            "Do your best catwalk across the room",
            "Share a harmless topic you have been curious about lately",
            "Reveal your wildest party story",
            "Do 20 squats while seductively staring at someone",
            "Pretend to call a fictional character and dramatically tell them you love them",
            "Act out an embarrassing video scene without recording it",
            "Dance to no music and pretend it's the best song",
            "Show everyone your most used emojis",
            "Do a dramatic reading of a completely fictional DM",
            "Give someone in the room three exaggerated compliments",
            "Let someone write anything on your arm",
            "Invent a ridiculous text to a fictional boss and read it aloud without sending it",
            "Do a modeling photoshoot for 30 seconds",
            "Reveal who you'd most likely date in this room",
            "Do the Macarena aggressively",
            "Challenge someone in the room to a harmless pose-off",
            "Read a ridiculous fake status aloud without posting it",
            "Do your best impression of someone flirting",
            "Act out a 10-second story video without recording or posting it",
            "Say 'we need to talk' in three different dramatic voices to the group",
            "Dance battle with someone for 1 minute"
        )
        
        // Add Sweet Couples dares (PG-13, no PIN needed)
        sweetDares.forEach { text ->
            allPrompts.add(PromptItem(
                type = PromptItemType.DARE,
                category = PromptCategory.COUPLES,
                difficulty = Difficulty.EASY,
                text = text,
                isSafeForKids = false
            ))
        }
        
        // Add Spicy Couples dares (Adults only)
        spicyDares.forEach { text ->
            allPrompts.add(PromptItem(
                type = PromptItemType.DARE,
                category = PromptCategory.COUPLES,
                difficulty = Difficulty.MEDIUM,
                text = text,
                isSafeForKids = false
            ))
        }
        
        // Add Party Wild dares (Adults only)
        wildDares.forEach { text ->
            allPrompts.add(PromptItem(
                type = PromptItemType.DARE,
                category = PromptCategory.PARTY,
                difficulty = Difficulty.EXTREME,
                text = text,
                isSafeForKids = false
            ))
        }
    }
    
    private fun addPartyDares() {
        // ========== EASY DARES (70+) ==========
        val easyDares = listOf(
            "Do your best celebrity impression for 30 seconds",
            "Speak in an accent for the next 2 rounds",
            "Do 10 jumping jacks right now",
            "Make a funny face and hold it for 20 seconds",
            "Give someone in the group a genuine compliment",
            "Do your best dance move",
            "Sing the chorus of any song",
            "Tell a joke (even a bad one)",
            "Imitate another player until someone guesses who",
            "Say the alphabet backwards as fast as you can",
            "Do your best animal impression",
            "Walk like a model across the room",
            "Let someone draw something on your hand",
            "Talk in a robot voice for 1 minute",
            "Do your best evil laugh",
            "Strike 3 different poses like a fashion model",
            "Say something nice about every player",
            "Pretend to be a news anchor reporting this game",
            "Do the chicken dance",
            "Tell everyone your most used emoji and why",
            "Do 10 air guitar riffs",
            "Speak only in questions for 2 rounds",
            "Make up a short rap about someone here",
            "Do your best impression of a baby",
            "Dance with no music for 30 seconds",
            "High-five everyone in the room",
            "Whisper everything you say for the next round",
            "Do your best superhero pose",
            "Yell everything you say for one round",
            "Do 5 pushups (or modified pushups)",
            "Stand on one foot for 30 seconds",
            "Hug the person to your left",
            "Give yourself a nickname for the rest of the game",
            "Talk like a pirate for 2 minutes",
            "Do your best slow-motion replay of catching a ball",
            "Make up a handshake with someone",
            "Compliment yourself out loud",
            "Act out your morning routine in 30 seconds",
            "Do your best opera singing",
            "Wave at an imaginary crowd like you just won something",
            "Speak in third person for 2 rounds",
            "Do your best villain introduction",
            "Let the group choose a silly nickname for you for two rounds",
            "Do 15 squats",
            "Make a toast to the group",
            "Say a tongue twister 3 times fast",
            "Act like you're in a silent movie for 1 minute",
            "Pat your head and rub your belly at the same time",
            "Do your best impression of a teacher",
            "Blow a kiss to everyone",
            "Do 30 seconds of shadow boxing",
            "Talk without using the letter 'E' for 1 minute",
            "Pretend to be a waiter taking everyone's order",
            "Tell a 30-second story about anything",
            "Do your best workout instructor impression",
            "Do your best game-show host introduction",
            "Do the worm (or attempt to)",
            "Say 'I love pizza' in the most dramatic way possible",
            "Be someone's hype person for the next round",
            "Moonwalk across the room",
            "Pretend you're a flight attendant giving safety instructions",
            "Do 10 star jumps",
            "Act like a monkey for 30 seconds",
            "Talk like you're narrating a documentary",
            "Do your best dramatic soap opera acting",
            "Make everyone laugh within 1 minute",
            "Do a trust fall with someone",
            "Say the most random thing you can think of",
            "Give a 30-second motivational speech",
            "Attempt to juggle any 3 items"
        )
        
        // ========== MEDIUM DARES (70+) ==========
        val mediumDares = listOf(
            "Create a fictional notification and read it dramatically",
            "Sing Happy Birthday as if you were making an over-the-top imaginary phone call",
            "Name five people or characters you would invite to a dream party",
            "Read a fictional text to an imaginary crush; do not send anything",
            "Do an embarrassing TikTok dance",
            "Exchange an item of clothing with someone for 3 rounds",
            "Let someone do your makeup blindfolded",
            "Tell a funny story about a harmless purchase without showing account history",
            "Do a safe superhero pose for 10 seconds",
            "Balance a small lightweight object on your head for 10 seconds",
            "Reveal your most used app this week",
            "Let someone style your hair however they want",
            "Dance to a song chosen by the group",
            "Do 30 seconds of interpretive dance to no music",
            "Describe a funny photo memory without opening your gallery",
            "Pretend to call the fifth character in a made-up contact list for 30 seconds",
            "Speak only in song lyrics for 3 rounds",
            "Strike a photo-ready pose; no recording or posting is required",
            "Reenact a scene from your favorite movie",
            "Do whatever the person to your right says for 1 round",
            "Let someone write something on your forehead",
            "Give a lap around the room doing a silly walk",
            "Admit your celebrity crush out loud",
            "Belly dance for 30 seconds",
            "Let the group invent a silly wallpaper theme without changing your device",
            "Reveal the last 3 things you googled",
            "Do an impression of someone until they guess it's them",
            "Let the group give you a makeover",
            "Howl like a wolf for 10 seconds",
            "Do your best fake crying performance",
            "Share an embarrassing photo of yourself",
            "Create a 30-second commercial for any product",
            "Serenade someone in the room",
            "Lead the group in five slow, comfortable stretches",
            "Do karaoke to a song chosen by the group",
            "Dance and freeze when someone says 'freeze'",
            "Speak in a made-up language for 2 minutes",
            "Let someone pick your outfit for tomorrow",
            "Act out your most embarrassing moment",
            "Do your best impression of each player",
            "Take a normal sip of water and give an over-the-top drink review",
            "Pretend you're proposing to someone in the room",
            "Invent a funny fictional contact name and explain who the character is",
            "Make up a dramatic last text and read it aloud",
            "Do 20 seconds of twerking (or attempt to)",
            "Let someone take an unflattering photo of you",
            "Describe how you imagine a mystery snack would taste without eating anything blindfolded",
            "Do five comfortable marching steps in place",
            "Wear socks on your hands for 3 rounds",
            "Make up a fictional search-history headline for a movie character",
            "Pretend to be a tour guide of the room",
            "Do a dramatic reading of a fictional last text",
            "Perform a magic trick (real or fake)",
            "Tell a story using only hand gestures",
            "Speak in posh British accent for 5 minutes",
            "Salsa dance with someone for 30 seconds",
            "Pretend you are live on social media for 30 seconds without recording or posting",
            "Lick your elbow (or try to)",
            "Have a staring contest with someone - loser does next dare",
            "Do a 10-second silly dance at a comfortable pace",
            "Invent a cringe-worthy fake social-media post and perform it",
            "Pretend you're on a cooking show making a sandwich",
            "Play one round of rock-paper-scissors with someone",
            "Dramatically recite a nursery rhyme",
            "Do your best cat impression for 1 minute",
            "Reenact a famous meme",
            "Let the group invent a funny caption for an imaginary photo",
            "Freestyle rap for 30 seconds about someone here",
            "Create a secret handshake with everyone",
            "Demonstrate your hidden talent"
        )
        
        // ========== HARD DARES (60+) ==========
        val hardDares = listOf(
            "Wear your clothes inside out for the rest of the game",
            "Hold a comfortable victory pose for 10 seconds",
            "Pretend to send a voice note to an alien, but do not contact anyone",
            "Let the group choose a harmless dance move for you",
            "Describe your ideal avatar without changing any account settings",
            "Act out a fake commercial for the nearest harmless object",
            "Tell the group one app feature you could not live without, without opening your phone",
            "Do 10 seconds of gentle marching in place, or choose a seated pose",
            "Speak everything backwards for 3 rounds",
            "Let the group ask you three harmless rapid-fire questions; you may pass on any",
            "Invent a ridiculous mystery-food combination and describe how it would taste",
            "Reveal your biggest secret to the group",
            "Write a status update for each player",
            "Impersonate a fictional boss or teacher reacting to a silly situation",
            "Hold a comfortable superhero pose for 10 seconds",
            "Make up a silly message to an imaginary contact and read it aloud",
            "Do your best slow-motion runway walk across the room",
            "Share the last embarrassing thing that happened to you",
            "Pretend to take a pizza order in a dramatic singing voice",
            "Do a short comfortable dance move, or choose a seated gesture",
            "Let someone pick a dare for you from the internet",
            "Be the group's over-the-top announcer for one round",
            "Read the last 10 texts in a group chat out loud",
            "Do your best twerk for 1 minute",
            "Let someone write anything on your arm with marker",
            "Impersonate everyone in the room for 2 minutes",
            "Describe an embarrassing photo memory without showing or opening your gallery",
            "Confess a fake crush to an imaginary movie character",
            "Do your best impression of a crying baby for 1 minute",
            "Let someone style you however they want",
            "Invent an embarrassing fake search and explain the imaginary context",
            "Do a 10-second superhero pose, or choose a seated gesture",
            "Keep a straight face while everyone tries to make you laugh",
            "Describe a memorable photo you like without showing the image",
            "Speak with your tongue out for 2 rounds",
            "Let someone choose a fictional username for you for this round only",
            "Tell the group your approximate screen-time guess without opening settings",
            "Do an embarrassing dare chosen by majority vote",
            "Do ten seconds of air guitar",
            "Do whatever the group decides for 5 minutes",
            "Reveal a secret about someone with their permission",
            "Do wall sit for 1 minute",
            "Create a fake headline about tonight's game",
            "Let someone draw on your face with marker",
            "Share your most embarrassing moment from this year",
            "Let the group pick a harmless accent for your next sentence",
            "Tell a two-line story using three words chosen by the group",
            "Pose like a statue until the next player is ready",
            "Use exaggerated hand gestures while talking for the next round",
            "Give the group a dramatic weather forecast for the room",
            "Reveal your Spotify top artists to everyone",
            "Do your most embarrassing talent",
            "Order something online that the group picks (under $5)",
            "Clean up after someone for a week (joke promise)",
            "Let the group invent a fictional account you would follow and explain why",
            "Voice your inner monologue for 2 minutes",
            "Let someone go through your camera for 2 minutes",
            "Say 'I love you' dramatically to an imaginary character",
            "Name a celebrity or fictional character whose public profile you find entertaining",
            "Let the group roast you for 1 minute"
        )
        
        // Add all Party dares
        easyDares.forEach { text ->
            allPrompts.add(PromptItem(
                type = PromptItemType.DARE,
                category = PromptCategory.PARTY,
                difficulty = Difficulty.EASY,
                text = text,
                isSafeForKids = false
            ))
            // Also add to Friends category
            allPrompts.add(PromptItem(
                type = PromptItemType.DARE,
                category = PromptCategory.FRIENDS,
                difficulty = Difficulty.EASY,
                text = text,
                isSafeForKids = false
            ))
        }
        
        mediumDares.forEach { text ->
            allPrompts.add(PromptItem(
                type = PromptItemType.DARE,
                category = PromptCategory.PARTY,
                difficulty = Difficulty.MEDIUM,
                text = text,
                isSafeForKids = false
            ))
            allPrompts.add(PromptItem(
                type = PromptItemType.DARE,
                category = PromptCategory.FRIENDS,
                difficulty = Difficulty.MEDIUM,
                text = text,
                isSafeForKids = false
            ))
        }
        
        hardDares.forEach { text ->
            allPrompts.add(PromptItem(
                type = PromptItemType.DARE,
                category = PromptCategory.PARTY,
                difficulty = Difficulty.HARD,
                text = text,
                isSafeForKids = false
            ))
            allPrompts.add(PromptItem(
                type = PromptItemType.DARE,
                category = PromptCategory.FRIENDS,
                difficulty = Difficulty.HARD,
                text = text,
                isSafeForKids = false
            ))
        }
    }
    
    private fun addKidsTruths() {
        // ========== KIDS SAFE (80+) ==========
        val kidsTruths = listOf(
            "If you were a superhero, what would your power be?",
            "What's the yuckiest food you've ever tried?",
            "If you could be any animal for a day, which would you pick?",
            "What's your favorite thing to do on a rainy day?",
            "If you could have any pet, what would it be?",
            "What's the funniest dream you've ever had?",
            "What's your favorite thing about school?",
            "If you could fly anywhere, where would you go?",
            "What's your favorite cartoon or show?",
            "If you found a magic lamp, what would you wish for?",
            "What's the silliest joke you know?",
            "What would you do if you were invisible for a day?",
            "What's your favorite ice cream flavor?",
            "If you could be in any video game, which one?",
            "What's something that makes you laugh every time?",
            "What's your favorite thing to do with your friends?",
            "If you could eat only one food forever, what would it be?",
            "What's your favorite holiday and why?",
            "If you could meet any cartoon character, who would it be?",
            "What's the coolest thing you've ever built?",
            "What's your favorite bedtime story?",
            "If you had a robot, what would you make it do?",
            "What's your favorite sport or game?",
            "What would you name a pet dinosaur?",
            "What's the best birthday party you've been to?",
            "If you could talk to animals, which would you talk to first?",
            "What's your favorite snack?",
            "If you could live in any book, which one would it be?",
            "What's the nicest thing someone did for you?",
            "What do you want to be when you grow up?",
            "What's your favorite color and why?",
            "If you could invent anything, what would it be?",
            "What's your favorite thing about your best friend?",
            "What makes you feel super happy?",
            "If you could swim with any sea creature, which one?",
            "What's the bravest thing you've ever done?",
            "What's your favorite movie of all time?",
            "If you could have any magical power, what would it be?",
            "What's the most fun game you've ever played?",
            "What would you do with a million dollars?",
            "What's your favorite thing about summer?",
            "If you could visit any planet, which one?",
            "What's the coolest animal you've ever seen?",
            "What's your favorite thing to eat for breakfast?",
            "If you were a teacher, what would you teach?",
            "What's the best gift you ever gave someone?",
            "What makes you feel brave?",
            "If you could be any age, what age would you be?",
            "What's your favorite thing to do outside?",
            "What's the funniest thing that happened to you?",
            "If you could have dinner with any character, who?",
            "What's your favorite song to dance to?",
            "What would your dream treehouse look like?",
            "What's the kindest thing you've done for someone?",
            "If you could time travel, where would you go?",
            "What's your favorite board game?",
            "What makes you a good friend?",
            "If you could only wear one color forever, which?",
            "What's the best thing about being a kid?",
            "What's your favorite pizza topping?",
            "If you were a monster, would you be scary or silly?",
            "What's your favorite thing about yourself?",
            "What would you do if you found treasure?",
            "What's the most amazing thing in nature?",
            "If you could make any rule, what would it be?",
            "What's your favorite way to be creative?",
            "What superhero would you want as a friend?",
            "What's the best dream you've had?",
            "If you could shrink, where would you explore?",
            "What's your favorite thing to build with LEGO?",
            "What's the coolest thing you've learned?",
            "If you could have any toy, what would it be?",
            "What makes your family special?",
            "What's the best adventure you've been on?",
            "If you were a chef, what would you cook?",
            "What animal sound can you do best?",
            "What's your favorite thing about weekends?",
            "If you could name a star, what would you call it?",
            "What's the silliest thing you've ever seen?",
            "What do you like most about your room?",
            "If you could be any age for a week, which age?"
        )
        
        kidsTruths.forEach { text ->
            allPrompts.add(PromptItem(
                type = PromptItemType.TRUTH,
                category = PromptCategory.KIDS,
                difficulty = Difficulty.EASY, // Kids are always easy
                text = text,
                isSafeForKids = true
            ))
        }
    }
    
    private fun addFamilyTruths() {
        // ========== FAMILY NIGHT (60+) ==========
        val familyTruths = listOf(
            "What's your proudest family memory?",
            "What family tradition do you love most?",
            "What's the funniest thing that happened at a family gathering?",
            "Who in the family makes you laugh the most?",
            "What's your favorite family meal?",
            "What's something you've learned from your parents?",
            "What's your favorite family vacation memory?",
            "What do you love most about family game nights?",
            "Who in the family gives the best hugs?",
            "What's a talent you got from someone in the family?",
            "What's your favorite thing about holidays with family?",
            "What's the best advice a family member gave you?",
            "Who in the family tells the best stories?",
            "What's a tradition you want to keep forever?",
            "What makes your family unique?",
            "What's your favorite photo of the family?",
            "Who in the family makes the best food?",
            "What's a song that reminds you of family?",
            "What do you appreciate most about your siblings?",
            "What's the best surprise the family ever planned?",
            "What family recipe is your favorite?",
            "What's something you want to do together as a family?",
            "Who taught you something important?",
            "What's your favorite family movie to watch together?",
            "What makes family dinners special?",
            "What's a funny nickname in the family?",
            "What family member do you look up to?",
            "What's the best game to play with family?",
            "What's a happy childhood memory with family?",
            "Who in the family is the biggest joker?",
            "What do you love about family road trips?",
            "What's something fun you want to try with family?",
            "Who encourages you the most?",
            "What's your favorite thing about Sundays with family?",
            "What tradition would you want to start?",
            "What makes this family strong?",
            "Who is the best at keeping secrets?",
            "What's the funniest thing a family member has said?",
            "What's your favorite family celebration?",
            "What do grandparents teach us best?",
            "What's a lesson family life taught you?",
            "Who in the family is the most adventurous?",
            "What's your favorite thing to do with your parents?",
            "What family outing was the most memorable?",
            "Who gives the best advice in the family?",
            "What does family mean to you?",
            "What's your favorite way to spend time together?",
            "Who has the best sense of humor?",
            "What's a favorite book you read as a family?",
            "What makes you grateful for your family?",
            "What's a goal you have for the family?",
            "Who is the family's biggest cheerleader?",
            "What's the best family trip you've taken?",
            "What do you love about family pizza nights?",
            "Who always makes you feel better?",
            "What's a family inside joke?",
            "What's your wish for the family's future?",
            "Who are you most like in the family?",
            "What's a family activity you never get tired of?",
            "What's your favorite memory from last year?"
        )
        
        familyTruths.forEach { text ->
            allPrompts.add(PromptItem(
                type = PromptItemType.TRUTH,
                category = PromptCategory.FAMILY,
                difficulty = Difficulty.EASY,
                text = text,
                isSafeForKids = true
            ))
        }
    }
    
    private fun addStudentsTruths() {
        // ========== STUDENTS/SCHOOL (60+) ==========
        val studentTruths = listOf(
            "Who's your favorite teacher and why?",
            "What's the most embarrassing thing that happened at school?",
            "What's your favorite subject?",
            "Have you ever cheated on a test? (Be honest!)",
            "What's your favorite school lunch?",
            "Who's your best friend at school?",
            "What's the funniest thing that happened in class?",
            "Have you ever fallen asleep in class?",
            "What's your least favorite subject?",
            "What's your go-to excuse for late homework?",
            "Who's your school crush? (No names needed!)",
            "What's the best school event you've been to?",
            "Have you ever gotten in trouble at school?",
            "What's your favorite after-school activity?",
            "What's the wildest rumor at your school?",
            "Who would you pick to sit next to in class?",
            "What's the hardest test you've taken?",
            "What's your morning routine for school?",
            "Have you ever skipped class?",
            "What's your favorite school memory?",
            "What clique do you belong to?",
            "Who's the funniest person in your class?",
            "What's your dream college or career?",
            "What's the most awkward thing that happened to you at school?",
            "Do you prefer online class or in-person?",
            "What's your study secret?",
            "Have you ever passed notes in class?",
            "What's the weirdest thing in your backpack?",
            "Who do you eat lunch with?",
            "What's your favorite thing about weekends?",
            "Have you ever had a teacher you couldn't stand?",
            "What's your favorite school club or team?",
            "What's the best excuse you've heard for missing school?",
            "Who helps you with homework?",
            "What's your school's best tradition?",
            "Have you ever been class clown?",
            "What's your favorite thing about your school?",
            "Who's the smartest person you know at school?",
            "What's the most challenging class you've taken?",
            "Do you prefer working alone or in groups?",
            "What's your biggest school goal?",
            "Have you ever been nervous for a presentation?",
            "What's your locker like (messy or neat)?",
            "What's the best field trip you've been on?",
            "Who inspires you at school?",
            "What subject do you wish you were better at?",
            "What's your after-school routine?",
            "Have you ever had a school rivalry?",
            "What's the best project you've worked on?",
            "Do you prefer tests or essays?",
            "What's your dream school trip?",
            "Who would you want to be partners with?",
            "What's the funniest answer you've given on a test?",
            "What's your school's biggest event?",
            "Have you made friends outside your grade?",
            "What's something you wish schools taught?",
            "Who's the best storyteller in class?",
            "What's your favorite spot at school?",
            "What's the most fun you've had at school?",
            "What's one thing you'd change about school?"
        )
        
        studentTruths.forEach { text ->
            allPrompts.add(PromptItem(
                type = PromptItemType.TRUTH,
                category = PromptCategory.STUDENTS,
                difficulty = Difficulty.EASY,
                text = text,
                isSafeForKids = true
            ))
        }
    }
    
    private fun addCouplesTruths() {
        // ========== EASY ROMANCE (50+) ==========
        val easyRomance = listOf(
            "What was your first impression of me?",
            "What made you first attracted to me?",
            "What's your favorite memory of us together?",
            "What song reminds you of us?",
            "What's your favorite thing about our relationship?",
            "When did you first realize you had feelings for me?",
            "What's the cutest thing I do without realizing?",
            "What's your favorite date we've been on?",
            "What's something I do that always makes you smile?",
            "If we had a song, what would it be?",
            "What's one thing you love about my personality?",
            "What's your favorite food to eat together?",
            "What's a small thing I do that makes you happy?",
            "What's the best gift I've given you?",
            "What's your favorite way to spend time with me?",
            "What's something you've learned from our relationship?",
            "What was our funniest moment together?",
            "What's a movie we should watch together?",
            "What's the first thing you noticed about me?",
            "If we could travel anywhere, where would you take me?",
            "What's your favorite physical feature of mine?",
            "What habit of mine do you find endearing?",
            "What's something you want to try together?",
            "What makes you feel most loved?",
            "If you could relive one moment with me, which would it be?",
            "What's the sweetest thing I've ever said to you?",
            "What's something you admire about me?",
            "What's your favorite inside joke we have?",
            "What would be your dream date with me?",
            "What's a tradition you want us to have?",
            "How do I make you feel special?",
            "What's your favorite thing about my appearance?",
            "When do you feel closest to me?",
            "What's one thing you never get tired of about me?",
            "What's the best text I've ever sent you?",
            "What's something I do that comforts you?",
            "If we were in a movie, what genre would it be?",
            "What's a song lyric that describes us?",
            "What's your favorite way I show affection?",
            "What's something you want to thank me for?",
            "What would you miss most if I went away?",
            "What's the best adventure we've had?",
            "What's a quality of mine that you want to learn?",
            "When do you feel most happy with me?",
            "What's a place that's special to us?",
            "What made you decide to be with me?",
            "What's your favorite part of our routine?",
            "What's a dream you want us to achieve together?",
            "What's the kindest thing I've done for you?",
            "How do I support you in the best way?",
            "What's something about us that makes you proud?",
            "What nickname do you secretly like me calling you?"
        )
        
        // ========== MEDIUM ROMANCE (50+) ==========
        val mediumRomance = listOf(
            "What's something you've never told me but want to?",
            "What do you find most attractive about me?",
            "What's a fear you have about our relationship?",
            "Is there a habit of mine you wish I'd change?",
            "What's something you'd change about our relationship?",
            "Have you ever been jealous of someone around me?",
            "What's the biggest sacrifice you'd make for me?",
            "What's a secret you've kept from me?",
            "When have I disappointed you the most?",
            "What do you wish I did more of?",
            "Have you ever doubted our relationship?",
            "What's something I don't know about your past relationships?",
            "What's the most romantic thing you want to do with me?",
            "What's your favorite way I touch you?",
            "What do you fantasize about us doing?",
            "Have you ever been embarrassed by me?",
            "What's a worry you have about our future?",
            "What's something you pretend to like for my sake?",
            "When did you feel most vulnerable with me?",
            "What's a topic we need to discuss but avoid?",
            "Have you ever compared me to your ex?",
            "What's the biggest compliment someone gave you about us?",
            "What do you miss most when we're apart?",
            "What's a boundary you're afraid to set with me?",
            "Have you ever felt unappreciated by me?",
            "What's something you find irresistible about me?",
            "What insecurities do you have about us?",
            "What's the most intimate moment we've shared?",
            "Have you ever tested me without me knowing?",
            "What do you think about before falling asleep with me?",
            "What's a promise you need from me?",
            "What's something you want me to say more often?",
            "Have you ever felt not good enough for me?",
            "What's the hardest thing about loving me?",
            "What do you find most challenging about our relationship?",
            "What's something I do that turns you on?",
            "When do you feel most connected to me physically?",
            "What's a dream you want to share with me?",
            "Have you ever thought about breaking up?",
            "What makes you feel insecure about me?",
            "What's the most honest thing you can tell me right now?",
            "What do you need more of from me emotionally?",
            "What's something about me you didn't expect to love?",
            "When have I made you feel the most loved?",
            "What's a topic we disagree on that bothers you?",
            "Have you ever felt like giving up on us?",
            "What's my most attractive quality?",
            "What do you want our relationship to look like in 5 years?",
            "When do you feel most ignored by me?",
            "What's a romantic gesture you secretly want?",
            "What makes you feel most secure in our relationship?",
            "What's something you're too shy to ask me?"
        )
        
        // ========== HARD/SPICY ROMANCE (50+) ==========
        val hardRomance = listOf(
            "What's your secret fantasy involving me?",
            "What's the most adventurous thing you want to try together?",
            "What drives you crazy about me in a good way?",
            "What's the most attractive thing I've ever done?",
            "When are you most physically attracted to me?",
            "What's something you want me to do to you?",
            "What's your deepest desire in our relationship?",
            "What's your favorite thing about our physical connection?",
            "What do you wish I'd initiate more?",
            "What's something you've always wanted to try with me?",
            "When do I look the most irresistible to you?",
            "What's a fantasy you've had about us?",
            "What's the most romantic dream you've had about me?",
            "What do you think about when you're longing for me?",
            "What's something I do that you can't resist?",
            "What's your favorite way to be close to me?",
            "What makes you feel most desired by me?",
            "What's something you want me to whisper to you?",
            "What's the most passionate moment we've shared?",
            "When do you want me the most?",
            "What's something about me you can't stop thinking about?",
            "What would make you feel more desired?",
            "What's your favorite physical memory of us?",
            "What do you wish I was more bold about?",
            "What's something you want to try that we haven't?",
            "When have you felt the most passion from me?",
            "What's something I could say to make you melt?",
            "What's an unexplored fantasy you have?",
            "What's the bravest romantic thing you want to do?",
            "What do you daydream about us doing?",
            "What's your deepest romantic wish?",
            "What making you feel most adored?",
            "What's a secret romantic side of you I don't know?",
            "What's the most tempting thing about me?",
            "When do you feel the strongest chemistry between us?",
            "What's something you're too shy to ask for?",
            "What would make you feel completely irresistible?",
            "What's your favorite way I show my desire for you?",
            "What's something you want more of between us?",
            "When do you feel most confident with me?",
            "What's a romantic scenario you've imagined?",
            "What's your deepest need from our relationship?",
            "What makes your heart race about me?",
            "What's the most daring thing you'd want to do with me?",
            "When have you felt the most attracted to me?",
            "What's something that would make our connection stronger?",
            "What's your favorite intimate moment with me?",
            "What do you crave most about me?",
            "What would make your wildest dreams come true with me?",
            "What's the most honest romantic confession you can make?",
            "What's something about us you've never said out loud?",
            "What do you love most about being physically close to me?"
        )
        
        // Add all Couples truths
        easyRomance.forEach { text ->
            allPrompts.add(PromptItem(
                type = PromptItemType.TRUTH,
                category = PromptCategory.COUPLES,
                difficulty = Difficulty.EASY,
                text = text,
                isSafeForKids = false // Couples content is not for kids
            ))
        }
        
        mediumRomance.forEach { text ->
            allPrompts.add(PromptItem(
                type = PromptItemType.TRUTH,
                category = PromptCategory.COUPLES,
                difficulty = Difficulty.MEDIUM,
                text = text,
                isSafeForKids = false
            ))
        }
        
        hardRomance.forEach { text ->
            allPrompts.add(PromptItem(
                type = PromptItemType.TRUTH,
                category = PromptCategory.COUPLES,
                difficulty = Difficulty.HARD,
                text = text,
                isSafeForKids = false
            ))
        }
    }
    
    private fun addFriendsTruths() {
        // ========== EASY TRUTHS (50+) ==========
        val easyTruths = listOf(
            "What's your all-time favorite movie and why?",
            "If you could have any superpower, what would it be?",
            "What's the best vacation you've ever been on?",
            "What's your go-to comfort food?",
            "If you won the lottery, what's the first thing you'd buy?",
            "What's your favorite song to sing in the shower?",
            "What celebrity would you want as your best friend?",
            "What's your favorite thing to do on a lazy Sunday?",
            "If you could live in any TV show, which would you choose?",
            "What's your most-used emoji?",
            "What's the last thing you searched on Google?",
            "What's your favorite holiday and why?",
            "If you could eat only one food forever, what would it be?",
            "What's your hidden talent?",
            "What's your dream job, money aside?",
            "What's the best gift you've ever received?",
            "If you could travel anywhere, where would you go?",
            "What's your favorite thing about yourself?",
            "What's your go-to karaoke song?",
            "What's your biggest pet peeve?",
            "What's the weirdest food combination you enjoy?",
            "If you were a pizza topping, which would you be?",
            "What's your favorite childhood memory?",
            "What's your morning routine like?",
            "What's the last show you binge-watched?",
            "If you could meet any historical figure, who would it be?",
            "What's your favorite ice cream flavor?",
            "What's the best advice you've ever received?",
            "What's your favorite board game?",
            "If you could be any animal, what would you be?",
            "What's your favorite season and why?",
            "What's your go-to midnight snack?",
            "What's the most spontaneous thing you've ever done?",
            "If you had to describe yourself in three words, what would they be?",
            "What's your favorite way to exercise?",
            "What's the last book you read?",
            "What's your dream car?",
            "If you could have dinner with anyone, who would it be?",
            "What's your favorite thing about weekends?",
            "What's your guilty pleasure TV show?",
            "If you could learn any skill instantly, what would it be?",
            "What's your favorite restaurant?",
            "What's your phone wallpaper right now?",
            "If you could be famous for something, what would it be?",
            "What's the most beautiful place you've visited?",
            "What's your favorite app on your phone?",
            "If you could switch lives with a friend for a day, who would it be?",
            "What's your favorite type of music?",
            "What's your ideal weekend plan?",
            "If you could only watch one movie genre, what would it be?",
            "What's your favorite childhood cartoon?",
            "What's your dream house like?"
        )
        
        // ========== MEDIUM TRUTHS (50+) ==========
        val mediumTruths = listOf(
            "What's your most embarrassing moment ever?",
            "Who was your first crush?",
            "What's a secret talent nobody knows about?",
            "What's the biggest lie you've ever told?",
            "What's something you're afraid of that others find silly?",
            "Have you ever stalked someone on social media?",
            "What's the most childish thing you still do?",
            "What's your worst habit?",
            "Have you ever had a crush on a friend's significant other?",
            "What's the longest you've gone without showering?",
            "What's your most unpopular opinion?",
            "Have you ever pretended to be sick to skip something?",
            "What's the weirdest dream you've ever had?",
            "What's something you've never told your parents?",
            "Have you ever lied in this game?",
            "What's the most embarrassing harmless online mistake you've made?",
            "What's your biggest insecurity about your personality?",
            "Have you ever had an imaginary friend?",
            "What's the worst fashion choice you've ever made?",
            "Have you ever been caught talking to yourself?",
            "What's a movie that made you cry?",
            "What's the pettiest reason you've ended a friendship?",
            "Have you ever snooped through someone's phone?",
            "What's the most embarrassing thing you've done for a crush?",
            "What's a skill you pretend to have but don't?",
            "Have you ever ghosted someone?",
            "What's your guilty pleasure song?",
            "What's the most awkward date you've been on?",
            "Have you ever re-gifted a present?",
            "What's the worst gift you've ever given?",
            "Have you ever said 'I love you' without meaning it?",
            "What's the most trouble you've gotten into?",
            "What do you judge people for that you also do?",
            "Have you ever been kicked out of somewhere?",
            "What's a belief you held that turned out to be wrong?",
            "What's the most money you've wasted on something?",
            "Have you ever faked being interested in someone's story?",
            "What's your most irrational fear?",
            "Have you ever pretended not to see someone to avoid them?",
            "What's something you've done that you'd judge others for?",
            "Have you ever made up a rumor?",
            "What's your worst texting fail?",
            "What's the longest grudge you've held?",
            "Have you ever peed in a pool?",
            "What's your most cringe-worthy social media post?",
            "Have you ever blamed someone else for something you did?",
            "What's a lie you tell on dating apps?",
            "What's the most embarrassing autocorrect you've sent?",
            "Have you ever unfriended someone over something petty?",
            "What's a promise you've broken?",
            "Have you ever pretended to like someone's cooking?",
            "What's the dumbest thing you've done to impress someone?"
        )
        
        // ========== HARD TRUTHS (50+) ==========
        val hardTruths = listOf(
            "What's a secret you've never told anyone here?",
            "What's the biggest regret of your life?",
            "Who in this room do you trust the least?",
            "What's something you've done that you're ashamed of?",
            "Have you ever cheated in a relationship?",
            "What's your biggest insecurity?",
            "What's a lie you've told a close friend?",
            "Who do you secretly dislike in this group?",
            "What's your deepest fear about the future?",
            "Have you ever stolen something?",
            "What's the meanest thing you've ever said to someone?",
            "Who's the worst person you've ever dated?",
            "What's something you'd change about your best friend?",
            "Have you ever lied to protect yourself at someone else's expense?",
            "What's a toxic trait you have?",
            "Who was your most recent search on social media?",
            "What's the most illegal thing you've ever done?",
            "What's a secret you know about someone here?",
            "Have you ever spread rumors about someone?",
            "What's something you pretend to be that you're not?",
            "Who do you think is the most attractive person here?",
            "What's the worst thing you've said behind someone's back?",
            "Have you ever betrayed a friend's trust?",
            "What's your most embarrassing secret?",
            "Who in this room has annoyed you the most?",
            "What's a relationship you regret?",
            "Have you ever lied about your feelings for someone?",
            "What's something you've never forgiven someone for?",
            "Who do you compare yourself to?",
            "What's the worst decision you've ever made?",
            "Have you ever wanted to cut someone off but didn't?",
            "What do you fake being confident about?",
            "What's the hardest truth you've had to accept?",
            "Have you ever been jealous of a friend's success?",
            "What's something you've done that you'd never admit to your family?",
            "Who's the one person you wish you'd never met?",
            "What's a secret you keep from everyone?",
            "Have you ever manipulated someone?",
            "What's the cruelest thing you've ever done?",
            "Who have you hurt the most in your life?",
            "What's a part of yourself you hide from others?",
            "Have you ever wished something bad on someone?",
            "What's your biggest failure?",
            "Who do you secretly resent?",
            "What's a truth about yourself you're not ready to face?",
            "Have you ever used someone for your own benefit?",
            "What's the worst thing you've done in a relationship?",
            "Who would you save first in an emergency from this group?",
            "What's something you're too scared to say out loud?",
            "Have you ever pretended to be someone you're not?",
            "What's the biggest sacrifice you've made for someone?",
            "Who in this room would you trust with your life?"
        )
        
        // Add all truths to database
        easyTruths.forEach { text ->
            allPrompts.add(PromptItem(
                type = PromptItemType.TRUTH,
                category = PromptCategory.FRIENDS,
                difficulty = Difficulty.EASY,
                text = text,
                isSafeForKids = true
            ))
        }
        
        mediumTruths.forEach { text ->
            allPrompts.add(PromptItem(
                type = PromptItemType.TRUTH,
                category = PromptCategory.FRIENDS,
                difficulty = Difficulty.MEDIUM,
                text = text,
                isSafeForKids = false
            ))
        }
        
        hardTruths.forEach { text ->
            allPrompts.add(PromptItem(
                type = PromptItemType.TRUTH,
                category = PromptCategory.FRIENDS,
                difficulty = Difficulty.HARD,
                text = text,
                isSafeForKids = false
            ))
        }
    }
    
    /**
     * Get truths by category and difficulty
     */
    fun getTruths(
        category: PromptCategory? = null,
        difficulty: Difficulty? = null
    ): List<PromptItem> {
        return allPrompts.filter { prompt ->
            prompt.type == PromptItemType.TRUTH &&
            (category == null || prompt.category == category) &&
            (difficulty == null || prompt.difficulty == difficulty || difficulty == Difficulty.MIXED)
        }
    }
    
    /**
     * Get dares by category and difficulty
     */
    fun getDares(
        category: PromptCategory? = null,
        difficulty: Difficulty? = null
    ): List<PromptItem> {
        return allPrompts.filter { prompt ->
            prompt.type == PromptItemType.DARE &&
            (category == null || prompt.category == category) &&
            (difficulty == null || prompt.difficulty == difficulty || difficulty == Difficulty.MIXED)
        }
    }
    
    /**
     * Get a random truth, avoiding recently shown ones
     */
    fun getRandomTruth(
        category: PromptCategory? = null,
        difficulty: Difficulty? = null
    ): PromptItem? {
        val eligiblePrompts = getTruths(category, difficulty)
            .sortedBy { it.playCount } // Prefer less-shown prompts
            
        if (eligiblePrompts.isEmpty()) return null
        
        // Take from bottom 50% of play counts for variety
        val halfSize = (eligiblePrompts.size / 2).coerceAtLeast(1)
        val prompt = eligiblePrompts.take(halfSize).random()
        prompt.playCount++
        return prompt
    }
    
    /**
     * Get a random dare, avoiding recently shown ones  
     */
    fun getRandomDare(
        category: PromptCategory? = null,
        difficulty: Difficulty? = null
    ): PromptItem? {
        val eligiblePrompts = getDares(category, difficulty)
            .sortedBy { it.playCount }
            
        if (eligiblePrompts.isEmpty()) return null
        
        val halfSize = (eligiblePrompts.size / 2).coerceAtLeast(1)
        val prompt = eligiblePrompts.take(halfSize).random()
        prompt.playCount++
        return prompt
    }
    
    /**
     * Get count of prompts
     */
    fun getPromptCount(type: PromptItemType? = null, category: PromptCategory? = null): Int {
        return allPrompts.count { prompt ->
            (type == null || prompt.type == type) &&
            (category == null || prompt.category == category)
        }
    }
}
