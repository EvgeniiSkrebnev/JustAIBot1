require: slotfilling/slotFilling.sc
  module = sys.zb-common
  
require: localPatterns.sc  

  
theme: /

    state: Welcome
        q!: $regex</start>
        q!: $hello
        random:
            a: Здравствуйте! Вам помочь с выбором репетитора?
            a: Здравствуйте! Я могу Вам помочь подобрать репетитора?
        
        state: Disagree
            q: $disagree
            a: Если понадобится помощь, то обращайтесь! 
            
        state: Agree
            q: $agree
            a: Отлично!
            a: Какой язык Вы хотите изучить? У нас есть репетиторы по английскому, французскому и немецкому языку.
            
            state: LangMatch
                q: $lang
                script:
                    $session.lang = $parseTree._lang
                a: Отличный выбор! Ищем преподователя по {{$session.lang}}
                go!: /ToWhom
                
            state: NoLangMatch
                event: noMatch
                a: К сожалению, у нас нет преподователей этого языка. 
        
    state: ToWhom
        a: Для кого ищете репетитора?
        
        state: ToMe
            q: $me
            script:
                $session.agent = $parseTree._me
            a: Ищем репетитора Вам.
            go!: /Why
            
        state: ToChild
            q: $toChild
            script:
                $session.agent = $parseTree._toChild
            a: Ищем репетитора {{$session.agent}}.
            go!: /Why
    
    state: Why
        a: С какой целью ищите репетитора?
        
        state: Because
            intent: /reason
            script:
                $session.reason = $parseTree._reason
            a: Причина: {{$session.reason}}
            go!: /Price
    
    state: Time
        a: В какое время вам удобнее?
        buttons:
            "В первой половине дня"
            "Во второй половине дня"
    state: Price
        a: Каков ваш бюджет на одно занятие?
        buttons:
            "До 700 рублей" -> /PriceEnd
            "700 - 1500 рублей" -> /PriceEnd
            "Более 1500 рублей" -> /PriceEnd
    
    state: PriceEnd
        script:
            $session.price = $request.query
        go!: /Native
        
    state: Native
        a: Вы хотите заниматься с носителем?
        buttons:
            "Да" -> /NativeEnd
            "Нет" -> /NativeEnd
            "Кто такой носитель?" -> /WhoIsNative
            
        state: WhoIsNative
            a: Носитель языка - это тот для кого иностранный язык является родным.
            go!: ./Native
            
    state: NativeEnd
            script:
                $session.native = $request.query
            go!: /Check
    
    state: Check
        a: Язык: {{$session.lang}}
        a: Кому: {{$session.agent}}
        a: Цель: {{$session.reason}}
        a: Цена: {{$session.price}}
        a: С носителем? {{$session.native}}
            
    state: Match
        event!: match
        a: {{$context.intent.answer}}

    state: Bye
        intent!: /пока
        a: Пока!