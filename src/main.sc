require: slotfilling/slotFilling.sc
  module = sys.zb-common
  
require: localPatterns.sc  
  
theme: /

    state: Start
        q!: $regex</start>
        a: Здравствуйте! Я могу Вам помочь подобрать репетитора?
        
        state: Agree
            q: $agree
            a: Отлично!
        
        state: Disagree
            q: $disagree
            a: Если понадобится помощь, то обращайтесь! 

    state: Hello
        intent!: /привет
        a: Здравствуйте! Вам помочь с выбором репетитора?

    state: LanguageNeed
        

    state: NoMatch
        event!: noMatch
        a: Я не понял. Вы сказали: {{$request.query}}

    state: Match
        event!: match
        a: {{$context.intent.answer}}

    state: Bye
        intent!: /пока
        a: Пока!