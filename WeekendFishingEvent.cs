using System;
using System.Linq;
using System.Threading.Tasks;
using LangLaByLDK.Hander;
using LangLaByLDK.InfoChar;
using LangLaByLDK.OOP;
using LangLaByLDK.SupportOOP;
using LangLaByLDK.IO;

namespace LangLaByLDK.EventServer
{
    /// <summary>
    /// Hoạt động câu cá cuối tuần (11:00 Chủ Nhật hàng tuần)
    /// </summary>
    public static class WeekendFishingEvent
    {
        public const string POINT_KEY = InfoEventPoint.DIEM_CAU_CA_CUOI_TUAN;
        public const short FishingTicketLockId = 499;   // Vé câu cá (ra đồ khóa)
        public const short FishingTicketUnLockId = 500; // Vé câu cá (ra đồ không khóa)
        private const short FishingMapId = 85;
        private const short FishingCy = 692;

        private static readonly short[] RewardItemIds = { 501, 502, 503, 504, 505, 506, 507, 508, 509, 510 };
        // Tổng 100, riêng 510 chỉ 3%
        private static readonly int[] RewardPercents = { 11, 11, 11, 11, 11, 11, 11, 10, 10, 3 };

        private static bool _isActive;
        private static DateTime _endTime;

        public static bool IsActive => _isActive;

        public static void Start(TimeSpan? duration = null)
        {
            if (_isActive)
            {
                return;
            }

            var playTime = duration ?? TimeSpan.FromHours(1);
            _endTime = DateTime.Now.Add(playTime);
            _isActive = true;

            ResetOnlinePlayerPoints();
            Server.Server.SendThongBaoFromServer("🎣 Hoạt động câu cá cuối tuần đã bắt đầu! Thời gian: 60 phút. Dùng vé câu cá tại Làng Đá (tọa độ y=692).");
        }

        public static void Stop(string reason = "Hoạt động câu cá cuối tuần đã kết thúc!")
        {
            if (!_isActive)
            {
                return;
            }

            _isActive = false;
            Server.Server.SendThongBaoFromServer(reason);
        }

        public static void Update()
        {
            if (_isActive && DateTime.Now >= _endTime)
            {
                Stop();
            }
        }

        /// <summary>
        /// Xử lý dùng vé câu cá. Trả về true nếu đã xử lý (đã hoặc chưa tiêu vật phẩm).
        /// </summary>
        public static bool TryUseFishingTicket(Character player, Item item, short bagIndex)
        {
            if (item == null || (item.Id != FishingTicketLockId && item.Id != FishingTicketUnLockId))
            {
                return false;
            }

            // Chặn spam nếu đang trong trạng thái câu
            if (player.InfoGame != null && player.InfoGame.IsCatchFishingTask)
            {
                player.SendMessage(UtilMessage.SendThongBao("Đang thả câu, vui lòng chờ hoàn tất!", Util.WHITE));
                return true;
            }

            if (!_isActive)
            {
                player.SendMessage(UtilMessage.SendThongBao("🎣 Hoạt động câu cá diễn ra 11:00 Chủ Nhật hàng tuần. Hãy đợi tới khung giờ này!", Util.YELLOW_MID));
                return true;
            }

            if (player.Info.MapId != FishingMapId || player.Info.Cy != FishingCy)
            {
                player.SendMessage(UtilMessage.SendThongBao("Hãy tới bến cá Làng Đá (Map 85) và đứng đúng vị trí (y = 692) để thả câu.", Util.YELLOW_MID));
                return true;
            }

            if (InventoryHander.GetCountNotNullBag(player) <= 0)
            {
                player.SendMessage(UtilMessage.SendThongBao("Hành trang cần ít nhất 1 ô trống trước khi câu cá.", Util.YELLOW_MID));
                return true;
            }

            // Bật trạng thái đang câu để chặn spam
            player.InfoGame.IsCatchFishingTask = true;

            // Thời gian chờ giống Java (3.5s - 7s)
            int waitTime = Util.NextInt(3500, 7000);
            player.SendMessage(UtilMessage.UseItemCanTime(waitTime, "Đang thả câu", 0, player.Info.IdUser, item.Id));

            Task.Run(async () =>
            {
                try
                {
                    await Task.Delay(waitTime);

                    if (!player.IsConnection || !player.InfoGame.IsCatchFishingTask)
                    {
                        return;
                    }

                    // Kiểm tra lại slot túi và vật phẩm
                    if (bagIndex < 0 || bagIndex >= player.Inventory.ItemBag.Count)
                    {
                        player.SendMessage(UtilMessage.SendThongBao("Vé câu cá không còn trong túi.", Util.YELLOW_MID));
                        return;
                    }

                    var ticket = player.Inventory.ItemBag[bagIndex];
                    if (ticket == null || (ticket.Id != FishingTicketLockId && ticket.Id != FishingTicketUnLockId))
                    {
                        player.SendMessage(UtilMessage.SendThongBao("Vé câu cá không hợp lệ hoặc đã di chuyển.", Util.YELLOW_MID));
                        return;
                    }

                    // Kiểm tra túi trống lần nữa
                    if (InventoryHander.GetCountNotNullBag(player) <= 0)
                    {
                        player.SendMessage(UtilMessage.SendThongBao("Hành trang cần ít nhất 1 ô trống trước khi câu cá.", Util.YELLOW_MID));
                        return;
                    }

                    bool isLockReward = ticket.Id == FishingTicketLockId;

                    // Tiêu vé
                    InventoryHander.RemoveItemBag(player, bagIndex);
                    ItemHander.MsgUseItemBag(player, ticket);

                    // Random phần thưởng
                    short rewardId = RandomRewardId();
                    Item reward = new Item(rewardId, isLockReward, false, 1);
                    InventoryHander.AddItemBag(player, reward, true);

                    // Exp nhỏ kèm thông báo vui
                    player.UpdateExp(50_000);

                    // Điểm sự kiện
                    player.EventPoint ??= new InfoEventPoint();
                    player.EventPoint.AddPoint(POINT_KEY, 1);
                    player.EventPoint.AddPoint(InfoEventPoint.DIEM_TIEU_XAI, 1);

                    // Thông báo khi ra đồ hiếm (tỷ lệ <=10%)
                    int rewardIndex = Array.IndexOf(RewardItemIds, rewardId);
                    if (rewardIndex >= 0 && RewardPercents[rewardIndex] <= 10)
                    {
                        player.SendMessage(UtilMessage.SendThongBao($"✨ Bạn vừa câu được {Data.DataServer.ArrItemTemplate[rewardId].Name}!", Util.WHITE));
                    }

                    // Gửi animation cầm cần (Message 7)
                    Message m = new Message(7);
                    m.WriteInt(player.Info.IdUser);
                    player.SendMessage(m);
                }
                catch (Exception ex)
                {
                    Util.ShowErr(ex);
                }
                finally
                {
                    player.InfoGame.IsCatchFishingTask = false;
                }
            });

            return true;
        }

        private static void ResetOnlinePlayerPoints()
        {
            var players = Server.Server.GetAllOnlinePlayers();
            foreach (var p in players.Where(p => p != null))
            {
                p.EventPoint ??= new InfoEventPoint();
                p.EventPoint.RemovePoint(POINT_KEY);
            }
        }

        private static short RandomRewardId()
        {
            int roll = Util.NextInt(0, 100);
            int acc = 0;
            for (int i = 0; i < RewardItemIds.Length; i++)
            {
                acc += RewardPercents[i];
                if (roll < acc)
                {
                    return RewardItemIds[i];
                }
            }
            return RewardItemIds.Last();
        }
    }
}

