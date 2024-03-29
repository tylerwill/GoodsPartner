package com.goodspartner.util;

import com.goodspartner.dto.OrderDto;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.goodspartner.util.DeliveryTimeRangeParser.parseDeliveryTimeFromComment;


public class DeliveryTimeRangeParserTest {

    /*
      Keywords :
      ДО 16:00
      ПІСЛЯ 10:00
      С 9 ДО 16:00
      С 10:00 ДО 12:00
      з 10:00
      з 13:00 по 14:00
      З 12:00 ДО 14:00
      Прийом товару з 9:00-16:00
      09:00 - 16:00
      Прийом товару з 9:00-16:00, ОБІД з 11:30-12:00
      з 12-14:00

      Invalid / Rare
      НЕ РАНІШЕ 15:00!!

      Conflict:
      ОБІД з 14:00 до 15:00
   */
    private final List<String> data = Arrays.asList(
            "бн, ДО 16:00, + декларація безпеки вантажу, + якісні документи"
            , "бн / ЗАМОРОЗКА КРУАСАН / 067  618 63 48 Петр/ Прийом товару з 9:00-16:00, ОБІД з 11:30-12:00 "
            , "бн/ ДОСТАВКА С 9 ДО 16:00"
            , "бн, ПІСЛЯ 10:00, тел. 063-350-54-23 Лиля; 050-330-88-84 Владлена"
            , "бн; доставка  з 10:00 працюють / 044 275 83 86"
            , "нал, з оплатою, до 13:00, тел. 063-288-57-77"
            , "бн, Нова Пошта: м. Бориспіль, відділення №1, отримувач Товариство з обмеженою відповідальністю ДО ЕНД КО КИЇВ, контактна особа Момут Олександр, тел. 050-351-31-54, доставку сплачує отримувач, бн"
            , "нал, ПІСЛЯ 15:00, б/о, тел. 096-372-10-62, Борис"
            , "бн, ДО 12:00 товар приймають"
            , "нал, з оплатою + забрати за минулу накладну 3408,18 грн - ВСЬОГО 4615,26 грн, ДО 15:00"
            , "нал, Нова Пошта: м. Очаків, відділення №1, отримувач Поліщук Вадим Борисович, тел. 050-058-43-68, доставку сплачує отримувач"
            , "нал, ПІСЛЯ 12:00, з оплатою, тел. 066-312-98-91, набрати за 1 годину"
            , "бн, ДО 14:00, 073-505-50-95, Олександр"
            , "бн, ВІДПОВІДНІ ДАТИ! ПОВЕРТАЮТЬ ДОКУМЕНТИ!!! ДО 11:00, тел. 067-247-72-32 Максим + якісні, + специфікації"
            , "бн, Нова Пошта: м.Львів, доставка на адресу: вул. Городоцька, 302,  тел. 050-417-07-83 Юрий Мисько, доставку сплачуе ГД"
            , "бн, Нова Пошта, Івано-Франківська обл, м.Яремче, с.Поляниця, уч.Щивки, буд.220 Отримувач: Західна філія ТОВ «Зірка Буковелю», код ЄДРПОУ 37819362 Контактна особа: Піскорський Володимир, 0668472737, оплачує ТОВ Гранде дольче, бн"
            , "бн+ +ТТН +сертифікати з 13:00 по 14:00 обід"
            , "нал, ДОСТАВКА З 16:00 до 19:00, з оплатою, СВІЖІ ТЕРМІНИ, тел. +38 063 326 53 03, звати Хайм, + якісні документи"
            , "нал, ДО 12:00, з оплатою, тел. 066-312-98-91, набрати за 1 годину"
            , "бн+ +ТТН +сертифікати з 13:00 по 14:00 обід"
            , "нал, ОБОВЯЗКОВО ЗРАНКУ ДО 10:00 за 30 хв до приїзду подзвонити клієнту  097 255 02 17 Юрій/ забрати гроші за попередню накладну 7 704,00"
            , "бн, Нова Пошта: м. Львів, відділення №38, отримувач Ніронович Маріне Сейранівна, тел. 093-607-22-45, доставку сплачує ГД"
            , "бн, Нова Пошта: м. Суми, відділення № 8 отримувач Рибалко Марина Юріївна, тел. 095-739-61-46, доставку сплачує ГД"
            , "ДОВОЗ Нова пошта, м.  Харків, склад № 1, отримувач: Капінус Юрій Олександрович, тел 067  554 66 25, оплачуєГД"
            , "бн  Нова пошта, м.  Харків, склад № 1, отримувач: Капінус Юрій Олександрович, тел 067  554 66 25, оплачуєГД"
            , "бн,+переробка ДО 14:00, 073-505-50-95, Олександр"
            , "бн,  Києво-Святошинський район, с. Софіївська Борщагівка, вул. Соборна, 114, літер Б корпус №2, Години прийому: 09:00 - 16:00. "
            , "бн, ПРИНИМАЮТ ТОЛЬКО С 10:00 ДО 12:00, диспечер для столбиков - 099-016-73-57, р-н Молодость - 097-704-13-19"
            , "бн, Нова Пошта: м. Рівне, вул. Київська 60, отримувач ТОВ Рамедас Україна, код ЄДРПОУ 41602890, номер контактної особи Артем, 0505196510, доставку сплачуэ отримувач"
            , "нал, з оплатою, ДО 15:00"
            , "нал, з оплатою, ДО 15:00"
            , "нал, Нова Пошта: с. Святопетрівське, відділення №1, отримувач Петрівська Олена Анатоліївна, тел. 097-713-51-58, доставку сплачує отримувач"
            , "бн, ЗАМОРОЗКА КРУАСАНИ, ДО 12:00, тел. 099-373-56-61, Елена"
            , "нал, з 10:00 до 17:00, з оплатою, тел. 063-854-04-62 Ярослав"
            , "бн, Нова Пошта, м. Запоріжжя, склад №45, отримувач ТОВ ДЖИ ЕФ СІ, контактна особа Панов Вадим Юрійович, тел (067) 614-32-48, ЄДРПОУ: 39219777, оплачує отримувач, бн"
            , "нал, Нова Пошта: м.Вишневе, відділення №5, отримувач Михайленко Сергій Анатолійович, тел. 067-208-77-22, оплачує отримувач нал"
            , "нал, Нова Пошта: м. Харьків, відділення №34, отримувач Поляков Анатолий Михайлович, тел. 0988857767, доставку сплачує отримувач"
            , "бн; доставка  з 10:00 працюють / 044 275 83 86"
            , "бн /ДОСТАВКА ДО 17:00, 095 857 44 00"
            , "нал, ДО 14:00, +ЯКІСНІ, тел. 097-936-24-78"
            , "бн  Нова пошта, м.  Харків, склад № 1, отримувач: Капінус Юрій Олександрович, тел 067  554 66 25, оплачуєГД"
            , "нал, Нова Пошта: Закарпаття, смт Тересва, відділення №1, отримувач Небола Олег Васильевич, тел. 068-142-62-12, доставку сплачуе ГД"
            , "бн / ЗАМОРОЗКА КРУАСАН / 067  618 63 48 Петр/ Прийом товару з 9:00-16:00, ОБІД з 11:30-12:00 "
            , "бн, Нова Пошта, Івано-Франківська обл, м.Яремче, с.Поляниця, уч.Щивки, буд.220 Отримувач: Західна філія ТОВ «Зірка Буковелю», код ЄДРПОУ 37819362 Контактна особа: Піскорський Володимир, 0668472737, оплачує ТОВ Гранде дольче, бн"
            , "бн, Нова Пошта: м. Хмельницький, відділення № 28, отримувач Фаненштель Сергій Павлович, тел. 068-202-00-00, доставку оплачує ГД"
            , "нал, Нова Пошта: м. Очаків, відділення №1, отримувач Поліщук Вадим Борисович, тел. 050-058-43-68, доставку сплачує ГД"
            , "нал, з оплатою + за минулу накладну 3408,18 грн, ВСЬОГО 4767,18 грн, ДО 15:00"
            , "нал  096 6268597 з 13:00 до 13:30 обід"
            , "бн  Нова пошта, м.  Харків, склад № 1, отримувач: Капінус Юрій Олександрович, тел 067  554 66 25, оплачуєГД"
            , "нал, Нова Пошта: м. Миколаїв, відділення № 7, отримувач Боровий Віктор Миколайович, тел. 066-253-55-63, доставку сплачує ГД (зразок)"
            , "бн+ +ТТН +сертифікати з 13:00 по 14:00 обід"
            , "бн,  Києво-Святошинський район, с. Софіївська Борщагівка, вул. Соборна, 114, літер Б корпус №2, Години прийому: 09:00 - 16:00.  + 5 НАКЛЕЙОК "
            , "бн /ДОСТАВКА ДО 17:00, 095 857 44 00+переробка"
            , "бн МАКАРОНС 0677899431 з 12-14:00 перегруз , предварительно набрать ."
            , "нал, ПІСЛЯ 15:00, ЗАБРАТИ 41 629,80грн. за попередні накладні, ця б/о, тел. 096-372-10-62, Борис"
            , "бн,  Києво-Святошинський район, с. Софіївська Борщагівка, вул. Соборна, 114, літер Б корпус №2, Години прийому: 09:00 - 16:00."
            , "нал, з оплатою, до 14:00, + якісні документи, 099-132-89-87, Євгеній"
            , "бн, ДО 16:00, + декларація безпеки вантажу, + якісні документи"
            , "бн, ВІДПОВІДНІ ДАТИ! ПОВЕРТАЮТЬ ДОКУМЕНТИ!!! ДО 11:00, тел. 067-247-72-32 Максим + якісні, + специфікації"
            , "бн, Броварьський р-н., с. Требухів, вул.Броварська, 27, Ориентир:  ресторан «НУР» (справа) – а  напротив повернуть налево, конт. лице  Кирилюк Максим 067–504–29–31. ПАЛЕТНЕ ВІДВАНТАЖЕННЯ"
            , "бн до 15:00 0675042274 ЗАБРАТИ ПІДПИСАНИЙ ДОГОВІР"
            , "бн  Нова пошта, м.  Харків, склад № 1, отримувач: Капінус Юрій Олександрович, тел 067  554 66 25, оплачуєГД"
            , "ЗРАЗОК бн  Нова пошта, м.  Харків, склад № 1, отримувач: Капінус Юрій Олександрович, тел 067  554 66 25, оплачуєГД"
            , "нал, ПІСЛЯ 15:00, ЗАБРАТИ 12 466,20грн. за попередні накладні, ця б/о, тел. 096-372-10-62, Борис"
            , "бн, м. Ірпінь, тел.: (066) 711-60-00"
            , "нал, Нова Пошта, м.  Миргород, склад № 1, отримувач:  Мальцев Олександр Вікторович, тел 067  555 48 61,  оплачує ГД"
            , "бн /ДОСТАВКА ДО 17:00, 095 857 44 00"
            , "бн, Нова пошта, м. Кропивницький., відділення №1, отримувач: ФОП, ІПН: 2265101495, Кошлатий Ярослав Анатолійович, тел: 067-520-96-06, оплачує ГД, бн . "
            , "нал, Нова Пошта: Хмельницька область, Чемеровецький район, с.Вільхівці, відділення №1, отримувач Монастирська Лілія Павлівна, 0972666487, доставку сплачує отримувач"
            , "бн, Якісні+ТТН, ОБІД з 14:00 до 15:00"
            , "бн, Нова Пошта: м. Хмельницький, відділення № 28, отримувач Фаненштель Сергій Павлович, тел. 068-202-00-00, доставку оплачує ГД"
            , "нал ДО 17:00 0932566111 обсерваторна 25"
            , "бн  + ЗРАЗКИ З ОФІСА , ПОКЛАСТИ В ПОСИЛКУ Нова пошта, м.  Харків, склад № 1, отримувач: Капінус Юрій Олександрович, тел 067  554 66 25, оплачуєГД"
            , "бн, ЗАМОРОЗКА КРУАСАНИ, ДО 12:00 обов'язково!, тел. 063-633-73-39"
            , "бн .Новая Почта ¶г. Днепр , Отделение  №15 ¶ООО  «Надежный партнер»,  (ЄДРПОУ 37088610)¶тел: 067 617 45 41, Кастнер Антон Олегович¶оплата  за доставку ГД"
            , "нал, Нова Пошта: м. Очаків, відділення №1, отримувач Поліщук Вадим Борисович, тел. 050-058-43-68, доставку сплачує отримувач"
            , "бн, ВІДПРАВИТИ НА ПАЛЕТІ, Нова Пошта: г.Запорожье, ул.Европейская,16 ДОСТАВКА НА АДРЕСС! получатель Мирзоян Оксана Витальевна, тел. 067-619-50-11, доставку оплачивает ГД"
            , "бн, ЗАМОРОЗКА КРУАСАНИ, ДО 12:00, тел. 099-373-56-61, Елена"
            , "нал, ПІСЛЯ 12:00, з оплатою, тел. 066-312-98-91, набрати за 1 годину"
            , "бн/ 093 697 40 83 до 15:00!!!!!!!!!"
            , "нал, з 10:00 до 17:00, з оплатою, тел. 063-854-04-62 Ярослав"
            , "бн, ДО 14:00, тел. 050-487-28-62, Вова"
            , "бн  ПЕРЕГРУЗ  Дані на авто:Бойко Андрій Іванович, Volkswagen crafter , ВО 9261 ВО 0972739810 Андрій"
            , "нал, Нова пошта, м.  Харків, склад № 1, отримувач: Капінус Юрій Олександрович, тел 067  554 66 25, оплачує ГД"
            , "нал, набрати Яну Седляр перед відгрузкою - чекаємо гроші, З 12:00 ДО 14:00, б/о, прохідна інституту, тел. Дмитрий 063-121-51-89"
            , "бн, Нова пошта, м. Львів,  вул. Генерала Юнаківа, 10,  отримувач: Витрикуш Юліан,  тел. 0676744554, оплата ГД, безнал, ТТН"
            , "бн, Якісні+ТТН, ОБІД з 14:00 до 15:00"
            , "нал, перегруз до 12:00, на Лівому березі в районі Лівобережної, 068-899-95-98, Ілля"
            , "бн  ПЕРЕГРУЗ  Дані на авто:Бойко Андрій Іванович, Volkswagen crafter , ВО 9261 ВО 0972739810 Андрій"
            , "нал, Нова Пошта: Луганська обл., м. Северодонецьк, відділення №7, отримувач Любар Анна Сергіївна, тел. 050-557-73-45, доставку сплачує отримувач"
            , "бн, + ЗРАЗКИ, Нова Пошта: смт. Дубове, Тячевський р-н, відділення №1, отримувач Вурста Сергій Сергійович, тел. 068-317-77-73, доставку оплачуе ГД"
            , "бн; доставка  з 10:00 працюють / 044 275 83 86"
            , "нал, Нова Пошта: м. Очаків, відділення №1, отримувач Поліщук Вадим Борисович, тел. 050-058-43-68, доставку сплачує отримувач"
            , "нал, ДО 16:00"
            , "бн /ДОСТАВКА ДО 17:00, 095 857 44 00"
            , "бн, ДО 16:00, + декларація безпеки вантажу, + якісні документи"
            , "нал, з оплатою, до 14:00, + якісні документи, 099-132-89-87, Євгеній"
            , "нал, ДО СПЛАТИ 2535,18 грн, ДО 15:00"
            , "бн до 15:00 0675042274 "
            , "бн; доставка  з 10:00 працюють / 044 275 83 86"
            , "бн, Броварьський р-н., с. Требухів, вул.Броварська, 27, Ориентир:  ресторан «НУР» (справа) – а  напротив повернуть налево, конт. лице  Кирилюк Максим 067–504–29–31. ПАЛЕТНЕ ВІДВАНТАЖЕННЯ"
            , "бн, Нова Пошта: м. Чернігів, склад № 5, отримувач ТОВ ЧЕРНІГІВСЬКИЙ ХЛІБОКОМБІНАТ №2 (код ЄДРПОУ 39468592), контактна особа Помилуйко Олександр Петрович, тел. 050-465-20-30, доставку оплачує ГД"
            , "нал ДО 17:00 0932566111 обсерваторна 25"
            , "бн, РЕСТОРАН МОЛОДІСТЬ! ПРИНИМАЮТ ТОЛЬКО С 10:00 ДО 12:00, диспечер для столбиков - 099-016-73-57, р-н Молодость - 097-704-13-19"
            , "бн  Нова пошта, м.  Харків, склад № 1, отримувач: Капінус Юрій Олександрович, тел 067  554 66 25, оплачуєГД"
            , "бн, Нова Пошта: м.Львів, доставка на адресу: вул. Городоцька, 302,  тел. 050-417-07-83 Юрий Мисько, доставку сплачуе ГД"
            , "бн, Нова пошта, Черкаська область, м.Ватутіне ,відділення №1,ТОВКомбінат баранкових виробів, 099-905-39-66 представник, ЄДРПОУ: 39398625, оплачує ГД, бн"
            , "бн/ДОСТАВКА ДО 12:00 050 338 04 64/"
            , "бн, ДО 12:00,  тел. 050-667-01-69"
            , "нал,  Нова пошта, м. Вінниця, склад № 12 отримувач: Лемішевський Олександр, тел 097 980 28 77, оплачує ГД ,безнал"
            , "нал, Нова Пошта: м. Очаків, відділення №1, отримувач Поліщук Вадим Борисович, тел. 050-058-43-68, доставку сплачує отримувач"
            , "нал, Нова Пошта: м. Тульчин, відділення № 3, отримувач Попроцкая Светлана, тел. 063-403-72-53, доставку сплачує отримувач "
            , "бн, НА  10:00!!!!!    РЦ Укрлогістики   №  замовлення 301403     в 3-х екземплярах  ТТН-ки  ЗАМОРОЗКА !  ОБОВЯЗКОВО ПРОКЛЕЇТИ ДВОМА ЕТИКЕТКАМИ ( АРТИКУЛ+ШТРИХ-КОД) "
            , "бн, Нова пошта, м. Львів,  вул. Генерала Юнаківа, 10,  отримувач: Витрикуш Юліан,  тел. 0676744554, оплата ГД, безнал, ТТН"
            , "нал,  Нова пошта, м. Вінниця, склад № 12 отримувач: Лемішевський Олександр, тел 097 980 28 77, оплачує ГД ,безнал"
            , "бн / ЗАМОРОЗКА КРУАСАН / 067  618 63 48 Петр/ Прийом товару з 9:00-16:00, ОБІД з 11:30-12:00 "
            , "нал, ОБОВЯЗКОВО ЗРАНКУ ДО 10:00 за 30 хв до приїзду подзвонити клієнту  097 255 02 17 Юрій/ забрати гроші за попередню накладну 7 704,00"
            , "бн, Нова Пошта: м. Днепр, склад № 1, отримувач ПП Грааль восток, тел. 066-009-36-81, доставку сплачує ГД/ ВКЛАСТИ ОРИГІНАЛИ "
            , "нал, Нова Пошта: м. Одеса, відділення №16, отримувач Костинюк Тетяна Дмитрівна, тел. 050-504-34-78, доставку сплачує отримувач"
            , "нал, Нова Пошта: Миколаїв, відділення №7, одержувач Боровой Виктор Николаевич, тел. 066-253-55-63, доставку сплачує ГД"
            , "нал/ ДОСТАВКА ДО 17:00+акт звірки"
            , "нал, ПІСЛЯ 15:00, ЗАБРАТИ 10 594,20грн. за попередні накладні, ця б/о, тел. 096-372-10-62, Борис"
            , "бн /ДОСТАВКА ДО 17:00, 095 857 44 00 ОБЕРЕЖНО щоб не побилися  Шоколадні палички"
            , "бн, ДО 14:00, 073-505-50-95, Олександр"
            , "бн, Нова Пошта: м.Львів, відділення № 76, отримувач ТОВ Фемелі Тайм , ЄДРПОУ 42718992, контактна особа Приставський В.В., тел. 098-231-02-00, доставку сплачує отримувач, бн"
            , "бн, Нова Пошта: Миколаївська обл., м. Первомайськ, вудділення № 1, отримувач Юрченко Олеся, тел. 068-082-33-60, доставку сплачує ГД  "
            , "нал, з оплатою, до 13:00, + якісні документи, 099-132-89-87, Євгеній   "
            , "бн, Нова Пошта: Миколаїв, відділення №1, одержувач Боровой Виктор Николаевич, тел. 066-253-55-63, доставку сплачує ГД"
            , "бн, Нова Пошта: м. Сарни, адресна доставка, вул. Ковельська 10, отримувач Мойсеєць Олександр Володимирович, тел. 0688010149, доставку сплачуэ ГД"
            , "бн, Нова Пошта: м.Одеса, пр-т Небесної Сотні 2, ТРЦ Сіті Центр, РК Папашон Кідс, отримувач ТОВ Бізнес Просперіті, ЄДРПОУ 42595429, контактна особа Болдецка Анна Володимирівна, тел. 0671886987, доставку сплачує отримувач, бн"
            , "нал, Нова Пошта: м. Очаків, відділення №1, отримувач Поліщук Вадим Борисович, тел. 050-058-43-68, доставку сплачує отримувач"
            , "бн, Нова пошта, м. Львів,  вул. Генерала Юнаківа, 10,  отримувач: Витрикуш Юліан,  тел. 0676744554, оплата ГД, безнал, ТТН"
            , "бн, ДО 12:00,  тел. 050-331-04-76 Олексій"
            , " бн, Нова пошта,  м. Миронівка, вул. Соборності, 61А   отримувач ТОВ ЕКО, контактна особа: Каплунська Наталія Михайлівна, тел 097 768 68 74, оплата ГД, безнал"
            , "бн, Нова Пошта: м.Одеса, пр-т Небесної Сотні 2, ТРЦ Сіті Центр, РК Папашон Кідс, отримувач ТОВ Бізнес Просперіті, ЄДРПОУ 42595429, контактна особа Болдецка Анна Володимирівна, тел. 0671886987, доставку сплачує отримувач, бн"
            , "нал, ДО 16:00"
            , "бн, ДО 15:00     "
            , "нал, Нова Пошта: м. Днепр, склад № 1, отримувач ПП Грааль восток, тел. 066-009-36-81, доставку сплачує ГД/ ВКЛАСТИ ОРИГІНАЛИ "
            , "нал вул. Якуба Коласа ,25  10:30 до 19:00"
            , "бн, Нова Пошта: м. Умань, Черкаська обл., адреса: вул. Володимера Мономаха, 6 (магазин Лагуна), тел. 097-052-13-12, доставку оплачує ГД"
            , "бн +ОБРАЗЦИ  Нова пошта, м. Харків, склад № 1, отримувач: Хлібний Двір  (тел 067 554 66 25), оплачує ГД"
            , "бн, + ДОГОВІР, + ЗРАЗКИ, Нова Пошта: м. Суми, відділення № 8 отримувач Рибалко Марина Юріївна, тел. 095-739-61-46, доставку сплачує ГД"
            , "бн, Нова Пошта: м. Хмельницький, відділення № 28, отримувач Фаненштель Сергій Павлович, тел. 068-202-00-00, доставку оплачує ГД"
            , "нал, Нова Пошта: Донецька обл., Бахмутський р-н, с. Новолуганськ, відділення №1, отримувач Кандибін Валерій Іванович, тел. 050-827-50-37,  доставку сплачуе ГД"
            , "бн, Нова Пошта: м. Бориспіль, відділення №1, отримувач Товариство з обмеженою відповідальністю ДО ЕНД КО КИЇВ, контактна особа Момут Олександр, тел. 050-351-31-54, доставку сплачує отримувач, бн"
            , "нал, !!! ДО СПЛАТИ 3292,72 ГРН !!!, ДО 15:00"
            , "бн /ДОСТАВКА ДО 17:00, 095 857 44 00"
            , "нал, до 12:00 +ЯКІСНІ"
            , "нал, перегруз до 12:00, на Лівому березі в районі Лівобережної, 068-899-95-98, Ілля"
            , "бн, ВІДПОВІДНІ ДАТИ! ПОВЕРТАЮТЬ ДОКУМЕНТИ!!! ДО 11:00, тел. 067-247-72-32 Максим + якісні, + специфікації"
            , "бн, ДО 11:00, тел. 098-602-31-52 - Максим"
            , "бн, Якісні+ТТН, ОБІД з 14:00 до 15:00"
            , "нал/ ДОСТАВКА С 9 ДО 16:00"
            , "нал, ПРИЙМАЮТЬ З 16:00 ДО 19:00, з оплатою, тел. +38 063 326 53 03, звати Хайм, + якісні документи   "
            , "бн Нова пошта, м. Вінниця, склад № 12 отримувач: Кузь Олександр Вячеславович, тел 0979802877., оплачує ГД ,безнал"
            , "нал,  Нова пошта, м. Вінниця, склад № 12 отримувач: Лемішевський Олександр, тел 097 980 28 77, оплачує ГД ,безнал"
            , "нал, Нова Пошта: Львівська обл, Мостиський район, м. Судова Вишня, відділення №1, отримувач Шпилька Ольга Зеновіївна, тел. 097-014-82-95, доставку сплачує ГД"
            , "бн, + ДОГОВІР, + ЗРАЗКИ, Нова Пошта: м. Суми, відділення № 5, отримувач Рибалко Марина Юріївна, тел. 095-739-61-46, доставку сплачує ГД"
            , "нал, з оплатою, ДО 15:00"
            , "бн/ДОСТАВКА ДО 12:00 050 338 04 64/"
            , "нал, ПІСЛЯ 15:00, ЗАБРАТИ 14 809,20грн. за попередню накладну, ця б/о, тел. 096-372-10-62, Борис"
            , "бн 0632931428  з 10:00"
            , "бн, Нова пошта, м. Львів,  вул. Генерала Юнаківа, 10,  отримувач: Витрикуш Юліан,  тел. 0676744554, оплата ГД, безнал, ТТН"
            , "бн /ДОСТАВКА ДО 17:00, 095 857 44 00+переробка"
            , "бн+ +ТТН +сертифікати з 13:00 по 14:00 обід"
            , "нал, Нова Пошта: м. Івано-Франківськ, відділення № 4, отримувач Ящеріцена Наталія Іванівна, тел. 066-115-36-91, доставку сплачує отримувач"
            , "бн, + ЗРАЗКИ, Нова Пошта: смт. Дубове, Тячевський р-н, відділення №1, отримувач Вурста Сергій Сергійович, тел. 068-317-77-73, доставку оплачуе ГД"
            , "бн, ДО 12:00, тел. 099-373-56-61, Елена"
            , "бн, Нова Пошта з Києва: м. Житомир, відділення № 12, отримувач Гурчин Олександр Миколайович, тел. 097-813-29-09, оплачує Гранде дольче, безнал"
            , "бн, Нова пошта, м.Львів, адресна доставка, вул. галицька 15, отримувач: Кіцай Василь  Андрійович , тел: (093)-854-98-49, оплачує ТОВ Гранде дольче, бн"
            , "нал, + ЗРАЗКИ, Нова Пошта: Львівська обл., смт.Івано-Франково, відділення №1, отримувач Ковальчук Ірина Романівна, тел. 067-949-52-72, доставку сплачуэ отримувач"
            , "бн / ЗАМОРОЗКА КРУАСАН / 067  618 63 48 Петр/ Прийом товару з 9:00-16:00, ОБІД з 11:30-12:00 "
            , "нал, з оплатою, до 14:00, + якісні документи, 099-132-89-87, Євгеній"
            , "бн, м. Ірпінь, тел.: (066) 711-60-00"
            , "бн, ВІДПОВІДНІ ДАТИ! ПОВЕРТАЮТЬ ДОКУМЕНТИ!!! ДО 11:00, тел. 067-247-72-32 Максим + якісні, + специфікації"
            , "бн, Нова Пошта: м. Умань, Черкаська обл., адреса: вул. Володимера Мономаха, 6 (магазин Лагуна), тел. 097-052-13-12, доставку оплачує ГД"
            , "нал, з оплатою, ДО 15:00"
            , "бн, РЕСТОРАН МОЛОДІСТЬ! ПРИНИМАЮТ ТОЛЬКО С 10:00 ДО 12:00, диспечер для столбиков - 099-016-73-57, р-н Молодость - 097-704-13-19"
            , " бн, Нова пошта,  м. Миронівка, вул. Соборності, 61А   отримувач ТОВ ЕКО, контактна особа: Каплунська Наталія Михайлівна, тел 097 768 68 74, оплата ГД, безнал"
            , "нал, ДО 12:00!!! тел. 098-065-77-80 , ТЕРМОБОКС МАКАРОНС!!!"
            , "бн   Нова пошта, м. Харків, склад № 1, отримувач: Хлібний Двір  (тел 067 554 66 25), оплачує ГД"
            , "бн, РЕСТОРАН МОЛОДІСТЬ! ПРИЙМАЮТЬ ТІЛЬКИ З 8:00 ДО 10:00, диспетчер для стовпчиків - 099-016-73-57, р-н Молодість - 097-704-13-19  "
            , "нал, Нова Пошта: м. Одеса, склад №15, отримувач Врадій Валерій Валерійович, тел (067) 558-76-97, доставку сплачує ГД"
            , "бн, Нова Пошта: м. Кропивницький, вул. Попова космонавта, 8, ЕПІЦЕНТР отримувач Русич Євреній Євгенійович, тел. 099-732-08-09, доствку сплачує ГД  "
            , "бн, Нова Пошта: Дніпропетровська обл., смт. Слобожанське, вул. Бабенка, 25, отримувач Гережа Ольга Григорівна, тел. 097-377-58-15, доставку сплачує ГД     "
            , "бн, Нова Пошта: Миколаїв, відділення №7, одержувач Боровой Виктор Николаевич, тел. 066-253-55-63, доставку сплачує ГД "
            , "бн, Нова Пошта: м. Львів, відділення №38, отримувач Ніронович Маріне Сейранівна, тел. 093-607-22-45, доставку сплачує отримувач"
            , "бн, ДО 14:00!!!  тел.  (093) 370-64-50"
            , "нал, ДО 14:00!!!, тел. (093) 370-64-50"
            , "бн, РЕСТОРАН МОЛОДІСТЬ! ПРИЙМАЮТЬ ТІЛЬКИ З 8:00 ДО 10:00, диспетчер для стовпчиків - 099-016-73-57, р-н Молодість - 097-704-13-19    "
            , "бн, НП: м. Миколаїв, Херсонське шосе, 1, ДЛЯ КАФЕ, отримувач Марченко Марьяна Миколаївна, тел. 095-150-17-45, доставку сплачує ГД     "
            , "бн / 067  618 63 48 Петр/ Прийом товару з 9:00-18:00, ОБІД з 11:30-12:00  , ЗАМОРОЗКА, "
            , "нал, З ОПЛАТОЮ,   з 12:00 до 15:00, набрати за 15 хв, тел. 063-288-57-77 (дзвонити на Вайбер)       "
            , "бн, Нова Пошта: Миколаївська обл., м. Первомайськ, ВІДДІЛЕНЯ №6,  отримувач Юрченко Олеся, тел. 068-082-33-60, доставку сплачує ГД         "
            , "бн, Нова Пошта: смт. Дубове, Тячевський р-н, відділення №1, отримувач Вурста Сергій Сергійович, тел. 068-317-77-73, доставку сплачує ГД    "
            , "бн, З 12:00 ДО 14:00, прохідна інституту, тел. Дмитрий 063-121-51-89       "
            , "нал, Нова Пошта: Луганська обл., м. Северодонецьк, відділення №7, отримувач Любар Анна Сергіївна, тел. 050-557-73-45, доставку сплачує отримувач"
            , "бн, ДО 16:00, + декларація безпеки вантажу, + якісні документи  "
            , "бн, Броварьський р-н., с. Требухів, вул.Броварська, 27, Ориентир:  ресторан «НУР» (справа) – а  напротив повернуть налево, конт. лице  Кирилюк Максим 067–504–29–31. ПАЛЕТНЕ ВІДВАНТАЖЕННЯ"
            , "бн, Нова Пошта: м. Чернігів, склад № 5, отримувач ТОВ ЧЕРНІГІВСЬКИЙ ХЛІБОКОМБІНАТ №2 (код ЄДРПОУ 39468592), контактна особа Помилуйко Олександр Петрович, тел. 050-465-20-30, доставку оплачує ГД     "
            , "нал, Нова Пошта: м. Чортків, відділення № 1, отримувач Подолянська Ірина, тел. 098-747-68-86, доставку сплачує отримувач     ТАРТИ- СКЛО ПІДПИСАТИ   "
            , "бн, Нова пошта, м.Львів, адресна доставка, вул. галицька 15, отримувач: Кіцай Василь  Андрійович , тел: (093)-854-98-49, оплачує ТОВ Гранде дольче, бн"
            , "нал З ОПЛАТОЮ  з 10:00 до 17:00, з оплатою, тел. 063-854-04-62; 067-434-33-55 Ярослав    "
            , "бн, Нова Пошта: Миколаївська обл., м. Первомайськ, ВІДДІЛЕНЯ №6,  отримувач Юрченко Олеся, тел. 068-082-33-60, доставку сплачує ГД         "
            , "бн, ДО 16:00, + декларація безпеки вантажу, + якісні документи"
            , "бн, Нова Пошта: м.Одеса, пр-т Небесної Сотні 2, ТРЦ Сіті Центр, РК Папашон Кідс, отримувач Ромасевич Ольга, тел 0979384046, доставку сплачує отримувач  "
            , "бн, Нова Пошта, м.  Миргород, склад № 1, отримувач:  Мальцев Олександр Вікторович, тел 067  555 48 61,  оплачує ГД"
            , "нал, ОБОВЯЗКОВО ЗРАНКУ ДО 10:00 за 30 хв до приїзду подзвонити клієнту  097 255 02 17 Юрій/ "
            , "бн, Нова Пошта: м. Суми, відділення № 8 отримувач Падалка Василь Миколайович, тел. 050-486-51-30, доставку сплачує ГД"
            , "бн, Якісні+ТТН, ОБІД з 14:00 до 15:00, тел.(067) 440-55-98"
            , "бн / + заказ за 08/12 по старим документам  +380632466332 / 063 392 71 62  Дмитро / ДО 12:00 !!!!!!!!!!!! / ЗА 1 ГОДИНУ до приїзду набрати"
            , "бн до 15:00 0675042274 "
            , "нал,   НЕ РАНІШЕ 15:00!!"
            , "бн до 15:00 0675042274"
            , "нал/ ДОСТАВКА ДО 17:00"
            , "нал, Нова Пошта: м. Львів, відділення №38, отримувач Ніронович Маріне Сейранівна, тел. 093-607-22-45, НАЛОЖ ПЛАТ на суму 787,00 грн, доставку сплачує отримувач"
            , "бн, ЗАМОРОЗКА КРУАСАНИ, ДО 12:00,  тел. 050-667-01-69"
            , "нал, ДО 11:30 АБО  ПІСЛЯ 16:00, набрати за 30хвилин, +ЯКІСНІ, тел. 097-464-29-25. Наталія, 096-146-54-17"
            , "нал, ПІСЛЯ 15:00, ЗАБРАТИ 15 582,60грн. за попередюі накладуі, ця б/о, тел. 096-372-10-62, Борис"

    );

    @Test
    public void testSpecificComment() {
        OrderDto orderDto = getOrderDtoWithComment("прийом товару з 11 до 12");
        parseDeliveryTimeFromComment(orderDto);
        Assertions.assertEquals(LocalTime.of(11, 0), orderDto.getDeliveryStart());
        Assertions.assertEquals(LocalTime.of(12, 0), orderDto.getDeliveryFinish());
    }

    @Test
    public void testAnotherSpecificComment() {
        OrderDto orderDto = getOrderDtoWithComment("нал, ПРАЦЮЮТЬ ДО з 9-30  до    17-00,");
        parseDeliveryTimeFromComment(orderDto);
        Assertions.assertEquals(LocalTime.of(9, 0), orderDto.getDeliveryStart());
        Assertions.assertEquals(LocalTime.of(17, 0), orderDto.getDeliveryFinish());
    }

    @NotNull
    private OrderDto getOrderDtoWithComment(String comment) {
        OrderDto orderDto = new OrderDto();
        orderDto.setComment(comment);
        return orderDto;
    }

    @Test
    public void testAllEntries() {
        List<OrderDto> orders = data.stream()
                .map(this::getOrderDtoWithComment).toList();

        orders.forEach(DeliveryTimeRangeParser::parseDeliveryTimeFromComment);

        List<OrderDto> matched = new ArrayList<>();
        List<OrderDto> unmatched = new ArrayList<>();

        orders.forEach(orderDto -> {
            if (orderDto.getDeliveryStart() == null
                    && orderDto.getDeliveryFinish() == null) {
                unmatched.add(orderDto);
            } else {
                matched.add(orderDto);
            }
        });

        Assertions.assertEquals(219, orders.size());
        Assertions.assertEquals(109, matched.size());
        Assertions.assertEquals(110, unmatched.size());
    }
}
