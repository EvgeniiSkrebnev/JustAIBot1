require: slotfilling/slotFilling.sc
  module = sys.zb-common
  
require: phoneNumber/phoneNumber.sc
  module = sys.zb-common 

require: namesRu/namesRu.sc
  module = sys.zfl-common   
  
require: localPatterns.sc  

require: patterns.sc
  module = sys.zb-common   
  
theme: /

    state: Welcome
        q!: $regex</start>
        q!: $hello
        random:
            a: Здравствуйте! Вам помочь с выбором репетитора?
            a: Здравствуйте! Я могу Вам помочь подобрать репетитора?
        buttons:
            "Да"
            "Нет"
        
        state: Disagree
            q: $disagree
            a: Если понадобится помощь, то обращайтесь! 
            
        state: Agree
            q: $agree
            a: Отлично!
            a: Какой язык Вы хотите изучить? Мы преподаем английский, французский и немецкий.
            
            state: LangMatch
                q: $lang
                script:
                    $session.lang = $parseTree._lang
                a: Отличный выбор! Ищем преподователя по {{$nlp.inflect($session.lang, 'datv')}}
                go!: /ToWhom
                
            state: NoLangMatch
                event: noMatch
                a: К сожалению, у нас нет преподователей этого языка. 
        
    state: ToWhom
        a: Для кого ищете репетитора? Себе или ребенку?
        
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
            a: Ищем репетитора {{$nlp.inflect($session.agent, 'datv')}}.
            go!: /Why
    
    state: Why
        a: С какой целью ищите репетитора?
        state: Because
            intent: /reason
            script:
                $session.reason = $parseTree._reason
            a: Причина: {{$session.reason}}
            go!: /Price
    
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
        go!: /Native
            
    state: NativeEnd
            script:
                $session.native = $request.query
            go!: /Check
    
    state: Check
        a: Язык: {{$session.lang}}
        a: Кому: {{$session.agent}}
        a: Цель: {{$session.reason}}
        a: Цена: {{$session.price}}
        a: С носителем? {{$session.native}}  # узнать всё ли верно и вывести кнопки если надо что-то изменить с переносом на нужный стейт 
        go!: /AskPhone
        
    state: AskPhone || modal = true
        a: Для продолжения введите, пожалуйста, ваш номер телефона. С вами свяжется специалст по подбору репетитора.
        buttons:
            "Отмена" -> /Cancel
            
        state: GetPhone
            q: * $mobilePhoneNumber *
            script:
                $temp.phone = $parseTree._mobilePhoneNumber;
            go!: /Confirm
            
        state: LocalCatchAll
            event: noMatch
            a: Что-то это не похоже на номер телефона...
            go!: ..
                
    state: Confirm
        script:
            $temp.phone = $temp.phone || $client.phone;
        a: Ваш номер - {{$temp.phone}}, верно?
        script:
            $session.probablyPhone = $temp.phone;
        buttons:
            "Да"
            "Нет"
            
        state: PhoneAgree
            q: (да/верно)
            script:
                $client.phone = $session.probablyPhone;
                delete $session.probablyPhone;
            go!: /AskName
            
        state: PhoneDisagree
            q: (нет/неверно/не верно)
            go!: /AskPhone
        
    state: Cancel
        q!: (отмена)
        a: Тогда мы не сможем с вами связаться.
        a: Хотите все-таки ввести номер?
        buttons:
            "Да" -> /AskPhone
            "Нет" 
            
        state: Email
            q: (нет)
            a: В таком случае вы можете написать нам на почту: customer@langschool.ru. Всего доброго!
            
    state: AskName
        a: Как к вам можно обращаться?
        
        state: GetName
            q: * $Name *
            script:
                $client.name = $parseTree._Name
            a: Спасибо за информацию, {{$client.name}}! В ближайшее время с вами свяжется специалист. Всего доброго!
        
    state: CatchAll || noContext = true
        event!: noMatch
        a: Простите, я не понял. Переформулируйте, пожалуйста.
        go!: {{$session.lastState}}
            
    state: Match
        event!: match
        a: {{$context.intent.answer}}

    state: Bye
        intent!: /пока
        a: Пока!