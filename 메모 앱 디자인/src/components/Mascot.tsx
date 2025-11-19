import { ArrowLeft, FileText, Zap, TrendingUp, Plus, BookOpen, GraduationCap } from 'lucide-react';
import { Button } from './ui/button';
import { Progress } from './ui/progress';

interface MascotProps {
  onBack: () => void;
  onCreateNote: () => void;
}

export function Mascot({ onBack, onCreateNote }: MascotProps) {
  const level = 5;
  const currentXP = 234;
  const maxXP = 500;
  const progress = (currentXP / maxXP) * 100;

  const stats = [
    { icon: FileText, label: '개념 노트', value: '12', color: 'bg-[#A855DD]' },
    { icon: Zap, label: '시험 횟수', value: '8', color: 'bg-[#FFE999]' },
    { icon: TrendingUp, label: '정답률', value: '85%', color: 'bg-[#D9B5FF]' },
  ];

  return (
    <div className="max-w-4xl mx-auto p-6 pb-8">
      {/* Back Button */}
      <Button
        onClick={onBack}
        variant="ghost"
        className="mb-6 text-[#A855DD] hover:text-[#9333CC] hover:bg-[#F5F0FB] rounded-xl"
      >
        <ArrowLeft className="w-5 h-5 mr-2" />
        뒤로가기
      </Button>

      {/* Header */}
      <div className="mb-8">
        <h1 className="text-[#A855DD] mb-2">똥그리</h1>
        <p className="text-[#D9B5FF]">똑똑한 암기 학습, 똥그리와 함께</p>
      </div>

      {/* Mascot Card */}
      <div className="bg-white border-2 border-[#E8DFF5] rounded-3xl p-8 mb-6 shadow-md">
        <div className="flex items-center justify-between">
          <div className="flex-1">
            <div className="text-sm text-[#D9B5FF] mb-2">마스코트 레벨</div>
            <div className="text-[#A855DD] mb-3">Lv. {level}</div>
            <div className="text-sm text-[#D9B5FF] mb-3">다음 레벨까지 {currentXP} XP</div>
            <div className="max-w-md">
              <Progress value={progress} className="h-3 bg-[#F5F0FB]">
                <div 
                  className="h-full bg-gradient-to-r from-[#A855DD] to-[#FFE999] rounded-full transition-all duration-300"
                  style={{ width: `${progress}%` }}
                />
              </Progress>
            </div>
          </div>
          <div className="ml-8">
            <div className="w-32 h-32 rounded-full bg-gradient-to-br from-[#FFF4CC] to-[#FFE999] flex items-center justify-center shadow-lg">
              <span className="text-6xl">🐥</span>
            </div>
            <div className="text-center mt-3 text-[#A855DD]">똥그리</div>
          </div>
        </div>
      </div>

      {/* Stats Grid */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
        {stats.map((stat) => (
          <div
            key={stat.label}
            className="bg-white border-2 border-[#E8DFF5] rounded-2xl p-6 hover:shadow-md transition-all duration-300 hover:scale-105"
          >
            <div className="flex items-center gap-4">
              <div className={`w-12 h-12 rounded-xl ${stat.color} flex items-center justify-center shadow-sm`}>
                <stat.icon className="w-6 h-6 text-white" />
              </div>
              <div>
                <div className="text-2xl text-[#A855DD] mb-1">{stat.value}</div>
                <div className="text-sm text-[#D9B5FF]">{stat.label}</div>
              </div>
            </div>
          </div>
        ))}
      </div>

      {/* Action Buttons */}
      <div className="space-y-3">
        <Button
          className="w-full bg-[#A855DD] hover:bg-[#9333CC] text-white rounded-2xl py-6 shadow-md hover:shadow-lg transition-all duration-300"
        >
          <BookOpen className="w-5 h-5 mr-2" />
          노트 관리
        </Button>
        
        <Button
          onClick={onCreateNote}
          className="w-full bg-[#FFE999] hover:bg-[#FFD966] text-white rounded-2xl py-6 shadow-md hover:shadow-lg transition-all duration-300"
        >
          <Plus className="w-5 h-5 mr-2" />
          새 노트 작성
        </Button>
        
        <Button
          variant="outline"
          className="w-full bg-white border-2 border-[#E8DFF5] hover:border-[#A855DD] text-[#A855DD] hover:bg-[#F5F0FB] rounded-2xl py-6 transition-all"
        >
          <GraduationCap className="w-5 h-5 mr-2" />
          시험 보기
        </Button>
      </div>
    </div>
  );
}